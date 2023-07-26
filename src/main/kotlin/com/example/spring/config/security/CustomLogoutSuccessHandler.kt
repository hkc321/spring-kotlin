package com.example.spring.config.security

import com.example.spring.application.service.member.JwtService
import com.example.spring.config.code.ErrorCode
import com.example.spring.domain.member.Jwt
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class CustomLogoutSuccessHandler(private val jwtService: JwtService): LogoutSuccessHandler {
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

        try {
            if (!jwtService.checkValidAccessHeader(request)) {
                throw JwtException("잘못된 accessToken 입니다.")
            }

            val accessToken =  jwtService.extractAccessToken(request)
            jwtService.checkAccessToken(accessToken)

            val claim = jwtService.extractClaims(accessToken)
            val email = claim.get("email", String::class.java)
            val expiration = claim.expiration.time

            jwtService.deleteRefreshTokenByEmail(email)
            jwtService.saveLogoutToken(accessToken, expiration)
            jwtService.setLogoutMessage(true, response)

        } catch (ex: ExpiredJwtException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED.name, "만료된 accessToken 입니다.")
        } catch (ex: JwtException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, ex.message)
        } catch (ex: NullPointerException) {
            setErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.NULL_POINTER.name, "토큰값이 비어있습니다.")
        } catch (ex: IllegalStateException) {
            var code = ""
            var message = ""

            when (ex.message) {
                Jwt.JWT_EXCEPTION + Jwt.ACCESS -> {
                    code = ErrorCode.JWT_EXCEPTION.name
                    message = "잘못된 accessToken 입니다."
                }

                Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS -> {
                    code = ErrorCode.ACCESS_TOKEN_EXPIRED.name
                    message = "만료된 accessToken 입니다."
                }

                else -> null
            }

            setErrorResponse(response, HttpStatus.BAD_REQUEST, code, message)
        } catch (ex: Exception) {
            log.warn("unknown error logout handler")
            setErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION.name, ex.message)
        }
    }

    /**
     * 에러 메시지 설정
     * */
    private fun setErrorResponse(res: HttpServletResponse, status: HttpStatus, errorType: String, message: String?) {
        jwtService.setErrorResponseMessage(res, status, errorType, message ?: "")
    }
}