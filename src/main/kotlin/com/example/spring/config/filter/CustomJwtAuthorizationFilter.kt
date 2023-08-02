package com.example.spring.config.filter

import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.application.service.slack.SlackService
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.CustomJwtAuthorizationFilterException
import com.example.spring.domain.member.Jwt
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.sentry.Sentry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.IllegalStateException

/**
 * 인가필터
 * */
class CustomJwtAuthorizationFilter(
    private val jwtService: JwtService,
    private val slackService: SlackService
) : OncePerRequestFilter() {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        filter(request, response, filterChain)
    }

    fun filter(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        log.info("Authorization filter")
        try {
            request.apply {
                if (checkValidHeader().not()) {
                    jwtService.setErrorResponseMessage(
                        response = response,
                        status = HttpStatus.BAD_REQUEST,
                        errorType = ErrorCode.HEADER_EXCEPTION.name,
                        message = "Authorization 헤더가 누락되었습니다."
                    )
                    return
                }

                val accessToken = jwtService.extractAccessToken(this)
                jwtService.checkAccessToken(accessToken)

                val claim = jwtService.extractClaims(accessToken)
                val email = claim.get("email", String::class.java)

                SecurityContextHolder.getContext().authentication = jwtService.getAuthentication(email)
            }
        } catch (ex: ExpiredJwtException) {
            val message = "만료된 accessToken 입니다."
            throw ExpiredJwtException(null, null, message)
        } catch (ex: JwtException) {
            throw ex
        } catch (ex: NullPointerException) {
            throw ex
        } catch (ex: MemberDataNotFoundException) {
            throw ex
        } catch (ex: IllegalStateException) {
            var code: String
            var message: String

            when (ex.message) {
                Jwt.JWT_EXCEPTION + Jwt.ACCESS -> {
                    code = ErrorCode.JWT_EXCEPTION.name
                    message = "잘못된 accessToken 입니다."
                }

                Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS -> {
                    code = ErrorCode.ACCESS_TOKEN_EXPIRED.name
                    message = "만료된 accessToken 입니다."
                }

                else -> {
                    Sentry.captureException(ex)
                    throw ex
                }
            }
            throw CustomJwtAuthorizationFilterException(code, message)
        } catch (ex: Exception) {
            log.warn("jwt unknown error")
            ex.printStackTrace()
            Sentry.captureException(ex)
            slackService.sendExceptionMessage(request, ex)
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

}
