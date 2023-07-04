package com.example.spring.config.filter

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsImpl
import com.example.spring.application.service.member.UserDetailsServiceImpl
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

/**
 * 인가필터
 * */
class CustomJwtAuthorizationFilter(
    private val jwtService: JwtService,
    private val memberUseCase: MemberUseCase,
    private val userDetailsServiceImpl: UserDetailsServiceImpl
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
                            message = "잘못된 헤더입니다."
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
        } catch (expiredJwtException: ExpiredJwtException) {
            throw expiredJwtException
        } catch (jwtException: JwtException) {
            throw jwtException
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
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
            check(jwtService.checkValidToken(access))
            check(jwtService.isTokenExpired(access).not())
            UserDetailsImpl(
                memberUseCase.readMember(
                    MemberUseCase.Commend.ReadCommend(jwtService.extractEmail(access))
                )
            )
        } else {
            check(jwtService.checkValidToken(access))
            check(jwtService.checkValidToken(refresh))
            check(jwtService.isTokenExpired(refresh).not())
            val member: Member = memberUseCase.findMemberByRefreshToken(
                MemberUseCase.Commend.FindMemberByRefreshTokenCommend(refresh)
            )
//            val expireIn7Day = jwtService.checkExpireInSevenDayToken(refresh)
//            if (expireIn7Day) reissueRefreshToken(member.username, response)
            reissueAccessToken(member.email, response)
            UserDetailsImpl(member)
        }

        return UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    }

    private fun reissueAccessToken(
        username: String,
        response: HttpServletResponse
    ) {
        log.info("reissue AccessToken")
        val reissuedAccessToken = jwtService.createAccessToken(username)
        jwtService.setHeaderOfAccessToken(response, reissuedAccessToken)
    }
}

data class TokenPair(
    val access: String,
    val refresh: String?,
)