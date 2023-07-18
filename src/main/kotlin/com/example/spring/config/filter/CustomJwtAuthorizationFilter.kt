package com.example.spring.config.filter

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.port.out.member.JwtRedisPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsImpl
import com.example.spring.application.service.member.UserDetailsServiceImpl
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.config.exception.CustomJwtAuthorizationFilterException
import com.example.spring.domain.member.Jwt
import com.example.spring.domain.member.Member
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.IllegalStateException

/**
 * 인가필터
 * */
class CustomJwtAuthorizationFilter(
    private val jwtService: JwtService,
    private val memberUseCase: MemberUseCase,
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
    private val jwtRedisPort: JwtRedisPort
) : OncePerRequestFilter() {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info("Authorization filter")
        try {
            request
                .apply {
                    if (checkValidHeader().not()) {
                        jwtService.setErrorResponseMessage(
                            response = response,
                            status = HttpStatus.BAD_REQUEST,
                            errorType = Jwt.HEADER_EXCEPTION,
                            message = "Authorization 헤더가 누락되었습니다."
                        )
                        return
                    }
                }
                .let {
                    if (jwtService.onlyAccessToken(request)) {
                        TokenPair(
                            jwtService.extractAccessToken(request),
                            null
                        )
                    } else {
                        TokenPair(
                            jwtService.extractAccessToken(request),
                            jwtService.extractRefreshToken(request)
                        )
                    }
                }
                .apply { SecurityContextHolder.getContext().authentication = getAuthentication(response) }
        } catch (ex: ExpiredJwtException) {
            val message = when (ex.claims.subject) {
                Jwt.ACCESS -> "만료된 accessToken 입니다."
                else -> "만료된 refreshToken 입니다."
            }
            throw ExpiredJwtException(null, null, message)
        } catch (ex: JwtException) {
            throw ex
        } catch (ex: NullPointerException) {
            throw ex
        } catch (ex: MemberDataNotFoundException) {
            throw ex
        } catch (ex: IllegalStateException) {
            var code = ""
            var message = ""

            when (ex.message) {
                Jwt.JWT_EXCEPTION + Jwt.ACCESS -> {
                    code = Jwt.JWT_EXCEPTION
                    message = "잘못된 accessToken 입니다."
                }

                Jwt.JWT_EXCEPTION + Jwt.REFRESH -> {
                    code = Jwt.JWT_EXCEPTION
                    message = "잘못된 refreshToken 입니다."
                }

                Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS -> {
                    code = Jwt.EXPIRED_EXCEPTION
                    message = "만료된 accessToken 입니다."
                }

                Jwt.EXPIRED_EXCEPTION + Jwt.REFRESH -> {
                    code = Jwt.EXPIRED_EXCEPTION
                    message = "만료된 refreshToken 입니다."
                }

                else -> throw ex
            }
            throw CustomJwtAuthorizationFilterException(code, message)
        } catch (ex: Exception) {
            log.warn("jwt unknown error")
            ex.printStackTrace()
            throw ex
        }
        filterChain.doFilter(request, response)
    }

    /**
     * request header 검증
     * */
    private fun HttpServletRequest.checkValidHeader(): Boolean {
        return (jwtService.checkValidAccessHeader(this) && !jwtService.checkValidRefreshHeader(this)) ||
                (jwtService.checkValidAccessHeader(this) && jwtService.checkValidRefreshHeader(this))
    }

    /**
     * 헤더에서 가져온 토큰 검사
     * 통과 시 인증정보 가져옴
     * */
    private fun TokenPair.getAuthentication(response: HttpServletResponse): UsernamePasswordAuthenticationToken {
        val principal = if (refresh == null) {
            check(jwtService.checkValidToken(access)) { Jwt.JWT_EXCEPTION + Jwt.ACCESS }
            check(jwtService.isTokenExpired(access).not()) { Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS }
            check(jwtRedisPort.hasLogout(access).not()) { Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS }

            val claim = jwtService.extractClaims(access)
            val memberId = claim.toMap()["id"] as Int
            val email = claim.toMap()["email"].toString()

            UserDetailsImpl(memberUseCase.readMember(MemberUseCase.Commend.ReadCommend(memberId, email)))
        } else {
            check(jwtService.checkValidToken(access)) { Jwt.JWT_EXCEPTION + Jwt.ACCESS }
            check(jwtService.checkValidToken(refresh)) { Jwt.JWT_EXCEPTION + Jwt.REFRESH }

            val claim = jwtService.extractClaims(refresh)
            val memberId = claim.toMap()["id"] as Int
            val email = claim.toMap()["email"].toString()

            val existRefreshToken: String? = jwtRedisPort.findRefreshTokenByEmail(email)
            check(existRefreshToken != null) { Jwt.EXPIRED_EXCEPTION + Jwt.REFRESH } // DB에서 토큰 존재 여부 검사

            check(
                jwtService.compareRefreshToken(
                    existRefreshToken,
                    refresh,
                    email
                )
            ) { Jwt.EXPIRED_EXCEPTION + Jwt.REFRESH } // DB의 토큰과 Header의 토큰 검사

            val member: Member = memberUseCase.readMember(MemberUseCase.Commend.ReadCommend(memberId, email))

            val expireIn7Day = jwtService.checkRefreshTokenExpireDate(7, claim.expiration)
            if (expireIn7Day) reissueRefreshToken(member, response)
            reissueAccessToken(member, response)

            UserDetailsImpl(member)
        }

        return UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    }

    private fun reissueAccessToken(
        member: Member,
        response: HttpServletResponse
    ) {
        log.info("reissue AccessToken")
        val reissuedAccessToken = jwtService.createAccessToken(member)
        jwtService.setHeaderOfAccessToken(response, reissuedAccessToken)
    }

    private fun reissueRefreshToken(
        member: Member,
        response: HttpServletResponse
    ) {
        log.info("reissue RefreshToken")
        val reissuedRefreshToken = jwtService.createRefreshToken(member)
        val expirationTime: Long = jwtService.extractClaims(reissuedRefreshToken).expiration.time

        jwtRedisPort.saveRefreshToken(member.email, reissuedRefreshToken, expirationTime)
        jwtService.setHeaderOfRefreshToken(response, reissuedRefreshToken)
    }
}

data class TokenPair(
    val access: String,
    val refresh: String?,
)