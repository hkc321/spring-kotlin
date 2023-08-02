package com.example.spring.config.filter

import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.application.service.slack.SlackService
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.CustomJwtAuthorizationFilterException
import com.example.spring.domain.member.Jwt
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.mockito.ArgumentMatchers.any
import org.springframework.http.HttpStatus
import java.lang.Exception
import java.lang.IllegalStateException

class CustomJwtAuthorizationFilterTest : BehaviorSpec({

    val jwtService = mockk<JwtService>()
    val slackService = mockk<SlackService>()
    val jwtAuthorizationExceptionFilter = mockk<JwtAuthorizationExceptionFilter>()
    val customJwtAuthorizationFilter = CustomJwtAuthorizationFilter(jwtService, slackService)

    given("a CustomJwtAuthorizationFilter") {

        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>()
        val filterChain = mockk<FilterChain>()

        When("Authorization header is missing") {
            every { jwtService.checkValidAccessHeader(request) } returns false
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.HEADER_EXCEPTION.name, "Authorization 헤더가 누락되었습니다." ) } just runs
            every { response.status } returns HttpStatus.BAD_REQUEST.value()

            customJwtAuthorizationFilter.filter(request, response, filterChain)

            Then("it should return 400 Bad Request") {
                response.status shouldBe HttpStatus.BAD_REQUEST.value()
                verify(exactly = 1) {
                    jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.HEADER_EXCEPTION.name, "Authorization 헤더가 누락되었습니다.")
                }
            }
        }

        When("accessToken is invalid") {

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "INVALID_ACCESS_TOKEN"
            every { jwtService.checkAccessToken("INVALID_ACCESS_TOKEN") } throws IllegalStateException(Jwt.JWT_EXCEPTION + Jwt.ACCESS)
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.JWT_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.JWT_EXCEPTION, "잘못된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "잘못된 accessToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()


            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe Jwt.JWT_EXCEPTION
                    this.message shouldBe "잘못된 accessToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("accessToken is expired or has logout") {
            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.checkAccessToken("VALID_ACCESS_TOKEN") } throws IllegalStateException(Jwt.EXPIRED_EXCEPTION + Jwt.ACCESS)

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe ErrorCode.ACCESS_TOKEN_EXPIRED.name
                    this.message shouldBe "만료된 accessToken 입니다."
                }

            }
        }

        When("accessToken is expired after check logout") {
            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            justRun { jwtService.checkAccessToken("VALID_ACCESS_TOKEN") }
            every { jwtService.extractClaims(any()) } throws ExpiredJwtException(any(), any(), any())

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<ExpiredJwtException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.message shouldBe "만료된 accessToken 입니다."
                }
            }
        }

        When("throw some exception") {
            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            justRun { jwtService.checkAccessToken("VALID_ACCESS_TOKEN") }
            justRun { slackService.sendExceptionMessage(any(), any()) }
            every { jwtService.extractClaims(any()) } throws Exception("something")

            Then("it should return 400 BAD_REQUEST") {
                shouldThrowUnit<Exception> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.message shouldBe "something"
                }
            }
        }

        When("accessToken's email doesn't exist") {
            val claims = mockk<Claims>()
            val email = "test"

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            justRun { jwtService.checkAccessToken("VALID_ACCESS_TOKEN") }
            every { jwtService.extractClaims(any()) } returns claims
            every { claims.get("email", String::class.java)} returns email
            every { jwtService.getAuthentication(email) } throws MemberDataNotFoundException()

            Then("it should return 400 BAD_REQUEST") {
                shouldThrowUnit<MemberDataNotFoundException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "사용자가 존재하지 않습니다."
                }
            }
        }

    }
})
