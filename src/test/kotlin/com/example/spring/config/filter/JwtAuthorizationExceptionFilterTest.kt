package com.example.spring.config.filter

import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.application.service.slack.SlackService
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.CustomJwtAuthorizationFilterException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class JwtAuthorizationExceptionFilterTest : BehaviorSpec({
    Given("JwtAuthorizationExceptionFilter") {
        val jwtService = mockk<JwtService>()
        val slackService = mockk<SlackService>()
        val jwtAuthorizationExceptionFilter = JwtAuthorizationExceptionFilter(jwtService, slackService)

        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>()
        val filterChain = mockk<FilterChain>()

        When("Throw ExpiredJwtException in filterChain.doFilter") {
            val exception = mockk<ExpiredJwtException>()

            every { filterChain.doFilter(request, response) } throws exception
            every { exception.message } returns "test ExpiredJwtException"
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED.name, "test ExpiredJwtException") }

            jwtAuthorizationExceptionFilter.filter(request, response, filterChain)

            Then("error message function execute") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_TOKEN_EXPIRED.name, "test ExpiredJwtException") }
            }
        }

        When("Throw JwtException in filterChain.doFilter") {
            val exception = mockk<JwtException>()

            every { filterChain.doFilter(request, response) } throws exception
            every { exception.message } returns "test JwtException"
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, "test JwtException") }

            jwtAuthorizationExceptionFilter.filter(request, response, filterChain)

            Then("error message function execute") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION.name, "test JwtException") }
            }
        }

        When("Throw CustomJwtAuthorizationFilterException in filterChain.doFilter") {
            val exception = mockk<CustomJwtAuthorizationFilterException>()

            every { filterChain.doFilter(request, response) } throws exception
            every { exception.message } returns "test CustomJwtAuthorizationFilterException"
            every { exception.code } returns "test error code"
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, "test error code", "test CustomJwtAuthorizationFilterException") }

            jwtAuthorizationExceptionFilter.filter(request, response, filterChain)

            Then("error message function execute") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, "test error code", "test CustomJwtAuthorizationFilterException") }
            }
        }

        When("Throw NullPointerException in filterChain.doFilter") {
            val exception = mockk<NullPointerException>()

            every { filterChain.doFilter(request, response) } throws exception
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.NULL_POINTER.name, "토큰값이 비어있습니다.") }

            jwtAuthorizationExceptionFilter.filter(request, response, filterChain)

            Then("error message function execute") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.NULL_POINTER.name, "토큰값이 비어있습니다.") }
            }
        }

        When("Throw MemberDataNotFoundException in filterChain.doFilter") {
            val exception = mockk<MemberDataNotFoundException>()

            every { filterChain.doFilter(request, response) } throws exception
            every { exception.message } returns "test MemberDataNotFoundException"
            every { exception.code.name } returns "test error code"
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, "test error code", "test MemberDataNotFoundException") }

            jwtAuthorizationExceptionFilter.filter(request, response, filterChain)

            Then("error message function execute") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, "test error code", "test MemberDataNotFoundException") }
            }
        }

        When("Throw Exception in filterChain.doFilter") {
            val exception = mockk<Exception>()

            every { filterChain.doFilter(request, response) } throws exception
            justRun { exception.printStackTrace() }
            justRun { slackService.sendExceptionMessage(request, exception) }
            every { exception.message } returns "test Exception"
            justRun { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION.name, "test Exception") }

            jwtAuthorizationExceptionFilter.filter(request, response, filterChain)

            Then("error message function execute") {
                verify(exactly = 1) { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION.name, "test Exception") }
            }
        }
    }
})
