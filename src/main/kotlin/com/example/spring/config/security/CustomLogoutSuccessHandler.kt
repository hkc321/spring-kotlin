package com.example.spring.config.security

import com.example.spring.application.port.out.member.JwtRedisPort
import com.example.spring.application.service.member.JwtService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomLogoutSuccessHandler(private val jwtRedisPort: JwtRedisPort, private val jwtService: JwtService): LogoutSuccessHandler {
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val accessToken =  jwtService.extractAccessToken(request)
        val claim = jwtService.extractClaims(accessToken)
        val email = claim.toMap()["email"].toString()
        val expiration = claim.expiration.time

        jwtRedisPort.deleteRefreshTokenByEmail(email)
        jwtRedisPort.saveLogoutToken(accessToken, expiration)
    }
}