package com.example.spring.config.security

import com.example.spring.application.service.member.JwtService
import com.example.spring.config.code.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(private val jwtService: JwtService) : AccessDeniedHandler {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        log.warn("[authority error] ${accessDeniedException.message}")

        jwtService.setErrorResponseMessage(response, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN.name, "권한이 없습니다.")
    }
}