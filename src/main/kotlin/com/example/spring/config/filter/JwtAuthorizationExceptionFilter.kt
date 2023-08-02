package com.example.spring.config.filter

import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.application.service.slack.SlackService
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.CustomJwtAuthorizationFilterException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.sentry.Sentry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT 예외처리 필터
 * */
class JwtAuthorizationExceptionFilter(
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
        try {
            filterChain.doFilter(request, response) // -> CustomJwtAuthorizationFilter 진행
        } catch (ex: ExpiredJwtException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED.name, ex)
        } catch (ex: JwtException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, ex)
        } catch (ex: CustomJwtAuthorizationFilterException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ex.code, ex)
        } catch (ex: NullPointerException) {
            setErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.NULL_POINTER.name, ex)
        } catch (ex: MemberDataNotFoundException) {
            setErrorResponse(response, HttpStatus.BAD_REQUEST, ex.code.name, ex)
        } catch (ex: Exception) {
            log.warn("exception filter unknown error")
            ex.printStackTrace()
            Sentry.captureException(ex)
            slackService.sendExceptionMessage(request, ex)
            setErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION.name, ex)
        }
    }

    /**
     * 에러 메시지 설정
     * */
    fun setErrorResponse(res: HttpServletResponse, status: HttpStatus, errorType: String, ex: Throwable) {
        when (errorType) {
            ErrorCode.NULL_POINTER.name -> jwtService.setErrorResponseMessage(res, status, errorType, "토큰값이 비어있습니다.")
            else -> jwtService.setErrorResponseMessage(res, status, errorType, ex.message ?: "")
        }
    }
}
