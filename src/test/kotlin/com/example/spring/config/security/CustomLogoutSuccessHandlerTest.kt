package com.example.spring.config.security

import com.example.spring.application.service.member.JwtService
import com.example.spring.config.code.ErrorCode
import com.example.spring.domain.member.Jwt
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import java.lang.IllegalStateException

class CustomLogoutSuccessHandlerTest : BehaviorSpec({

    given("a CustomLogoutSuccessHandler") {
        val jwtService = mockk<JwtService>()
        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>()
        val authentication = mockk<Authentication>()

        val customLogoutSuccessHandler = CustomLogoutSuccessHandler(jwtService)

        When("access token is wrong") {
            every { jwtService.checkValidAccessHeader(request) } returns false
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, "잘못된 accessToken 입니다.") }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, "잘못된 accessToken 입니다.") }
            }
        }

        When("access token is invalid") {
            val expiredAccessToken = "expiredAccessToken"

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.extractAccessToken(request) } returns expiredAccessToken
            every { jwtService.checkAccessToken(expiredAccessToken) } throws IllegalStateException(Jwt.JWT_EXCEPTION + Jwt.ACCESS)
            justRun { jwtService.setErrorResponseMessage(any(), any(), any(), any()) }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION.name, "잘못된 accessToken 입니다.") }
            }
            And("it should not set the logout message") {
                verify(exactly = 0) { jwtService.deleteRefreshTokenByEmail(any()) }
                verify(exactly = 0) { jwtService.saveLogoutToken(any(), any()) }
                verify(exactly = 0) { jwtService.setLogoutMessage(any(), any()) }
            }
        }

        When("access token is expired or has logout") {
            val expiredAccessToken = "expiredAccessToken"

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.extractAccessToken(request) } returns expiredAccessToken
            every { jwtService.checkAccessToken(expiredAccessToken) } throws IllegalStateException(Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS)
            justRun { jwtService.setErrorResponseMessage(any(), any(), any(), any()) }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.ACCESS_TOKEN_EXPIRED.name, "만료된 accessToken 입니다.") }
            }
            And("it should not set the logout message") {
                verify(exactly = 0) { jwtService.deleteRefreshTokenByEmail(any()) }
                verify(exactly = 0) { jwtService.saveLogoutToken(any(), any()) }
                verify(exactly = 0) { jwtService.setLogoutMessage(any(), any()) }
            }
        }

        When("access token is empty") {
            val accessToken = ""

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.extractAccessToken(request) } returns accessToken
            justRun { jwtService.checkAccessToken(accessToken) }
            every { jwtService.extractClaims(accessToken) } throws NullPointerException()
            justRun { jwtService.setErrorResponseMessage(any(), any(), any(), any()) }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.NULL_POINTER.name, "토큰값이 비어있습니다.") }
            }
            And("it should not set the logout message") {
                verify(exactly = 0) { jwtService.deleteRefreshTokenByEmail(any()) }
                verify(exactly = 0) { jwtService.saveLogoutToken(any(), any()) }
                verify(exactly = 0) { jwtService.setLogoutMessage(any(), any()) }
            }
        }

        When("access token's signature error") {
            val accessToken = "accessToken"

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.extractAccessToken(request) } returns accessToken
            justRun { jwtService.checkAccessToken(accessToken) }
            every { jwtService.extractClaims(accessToken) } throws JwtException("test")
            justRun { jwtService.setErrorResponseMessage(any(), any(), any(), any()) }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, "test") }
            }
            And("it should not set the logout message") {
                verify(exactly = 0) { jwtService.deleteRefreshTokenByEmail(any()) }
                verify(exactly = 0) { jwtService.saveLogoutToken(any(), any()) }
                verify(exactly = 0) { jwtService.setLogoutMessage(any(), any()) }
            }
        }

        When("access token's some exception") {
            val accessToken = "accessToken"

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.extractAccessToken(request) } returns accessToken
            justRun { jwtService.checkAccessToken(accessToken) }
            every { jwtService.extractClaims(accessToken) } throws Exception("test")
            justRun { jwtService.setErrorResponseMessage(any(), any(), any(), any()) }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION.name, "test") }
            }
            And("it should not set the logout message") {
                verify(exactly = 0) { jwtService.deleteRefreshTokenByEmail(any()) }
                verify(exactly = 0) { jwtService.saveLogoutToken(any(), any()) }
                verify(exactly = 0) { jwtService.setLogoutMessage(any(), any()) }
            }
        }

        When("access token expired after check") {
            val accessToken = "accessToken"

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.extractAccessToken(request) } returns accessToken
            justRun { jwtService.checkAccessToken(accessToken) }
            every { jwtService.extractClaims(accessToken) } throws ExpiredJwtException(null, null,"test")
            justRun { jwtService.setErrorResponseMessage(any(), any(), any(), any()) }

            customLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)

            Then("it should set Error response") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED.name, "만료된 accessToken 입니다.") }
            }
            And("it should not set the logout message") {
                verify(exactly = 0) { jwtService.deleteRefreshTokenByEmail(any()) }
                verify(exactly = 0) { jwtService.saveLogoutToken(any(), any()) }
                verify(exactly = 0) { jwtService.setLogoutMessage(any(), any()) }
            }
        }
    }

})
