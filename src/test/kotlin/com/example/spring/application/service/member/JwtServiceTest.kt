package com.example.spring.application.service.member

import com.example.spring.application.port.out.member.JwtRedisPort
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.exception.JwtRenewException
import com.example.spring.config.code.ErrorCode
import com.example.spring.domain.member.Jwt
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class JwtServiceTest : BehaviorSpec({
    val objectMapper = mockk<ObjectMapper>()
    val jwtRedisPort = mockk<JwtRedisPort>()
    val memberJpaPort = mockk<MemberJpaPort>()
    val jwtServiceMockk = mockk<JwtService>()

    @InjectMockKs
    val jwtService = JwtService(objectMapper, jwtRedisPort, memberJpaPort)

    given("renewToken") {
        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>()

        When("access token is missing") {
            every { request.getHeader(Jwt.ACCESS_TOKEN_HEADER) } returns null

            Then("it should throw JwtRenewException") {
                shouldThrowUnit<JwtRenewException> {
                    jwtService.renewToken(request, response)
                }.apply {
                    this.status shouldBe HttpStatus.BAD_REQUEST
                    this.code shouldBe ErrorCode.HEADER_EXCEPTION
                    this.message shouldBe "${Jwt.ACCESS_TOKEN_HEADER} 헤더가 누락되었습니다."
                }
            }
        }

        When("access token is not start with Bearer ") {
            val testString = "test"

            every { request.getHeader(Jwt.ACCESS_TOKEN_HEADER) } returns testString

            Then("it should throw JwtRenewException") {
                shouldThrowUnit<JwtRenewException> {
                    jwtService.renewToken(request, response)
                }.apply {
                    this.status shouldBe HttpStatus.BAD_REQUEST
                    this.code shouldBe ErrorCode.HEADER_EXCEPTION
                    this.message shouldBe "${Jwt.ACCESS_TOKEN_HEADER} 헤더가 누락되었습니다."
                }
            }
        }

        When("refresh token is missing") {
            every { request.getHeader(Jwt.ACCESS_TOKEN_HEADER) } returns "${Jwt.TOKEN_PREFIX}testToken"
            every { request.getHeader(Jwt.REFRESH_TOKEN_HEADER) } returns null

            Then("it should throw JwtRenewException") {
                shouldThrowUnit<JwtRenewException> {
                    jwtService.renewToken(request, response)
                }.apply {
                    this.status shouldBe HttpStatus.BAD_REQUEST
                    this.code shouldBe ErrorCode.HEADER_EXCEPTION
                    this.message shouldBe "${Jwt.REFRESH_TOKEN_HEADER} 헤더가 누락되었습니다."
                }
            }
        }

        When("refresh token is not start with Bearer ") {
            every { request.getHeader(Jwt.ACCESS_TOKEN_HEADER) } returns "${Jwt.TOKEN_PREFIX}testToken"
            every { request.getHeader(Jwt.REFRESH_TOKEN_HEADER) } returns "testToken"

            Then("it should throw JwtRenewException") {
                shouldThrowUnit<JwtRenewException> {
                    jwtService.renewToken(request, response)
                }.apply {
                    this.status shouldBe HttpStatus.BAD_REQUEST
                    this.code shouldBe ErrorCode.HEADER_EXCEPTION
                    this.message shouldBe "${Jwt.REFRESH_TOKEN_HEADER} 헤더가 누락되었습니다."
                }
            }
        }

        When("access token is invalid") {
            every { request.getHeader(Jwt.ACCESS_TOKEN_HEADER) } returns "${Jwt.TOKEN_PREFIX}testToken"
            every { request.getHeader(Jwt.REFRESH_TOKEN_HEADER) } returns "${Jwt.TOKEN_PREFIX}testToken"
            every { jwtServiceMockk.extractAccessToken(request) } returns "accessToken"
            every { jwtServiceMockk.extractRefreshToken(request) } returns "refreshToken"
            every { jwtServiceMockk.checkValidToken(any(), Jwt.ACCESS) } returns false


            Then("it should throw JwtRenewException") {
                shouldThrowUnit<JwtRenewException> {
                    jwtService.renewToken(request, response)
                }.apply {
                    this.status shouldBe HttpStatus.UNAUTHORIZED
                    this.code shouldBe ErrorCode.JWT_EXCEPTION
                    this.message shouldBe "잘못된 accessToken 입니다."
                }
            }
        }
    }
})
