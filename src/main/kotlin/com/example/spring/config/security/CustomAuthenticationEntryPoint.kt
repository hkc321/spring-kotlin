package com.example.spring.config.security

import com.example.spring.application.service.member.JwtService
import com.example.spring.domain.member.Jwt
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(private val jwtService: JwtService): AuthenticationEntryPoint {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.warn("[authentication error] ${authException.message}")

        val exceptionMessage = request.getAttribute(Jwt.EXCEPTION).toString()
        jwtService.setResponseMessage(false, response, exceptionMessage)
    }
}