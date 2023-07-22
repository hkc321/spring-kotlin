package com.example.spring.config.filter

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.port.out.member.JwtRedisPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsServiceImpl
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.CustomJwtAuthorizationFilterException
import com.example.spring.domain.member.Jwt
import com.example.spring.domain.member.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import java.util.*

class CustomJwtAuthorizationFilterTest : BehaviorSpec({

    val jwtService = mockk<JwtService>()
    val memberUseCase = mockk<MemberUseCase>()
    val userDetailsServiceImpl = mockk<UserDetailsServiceImpl>()
    val jwtRedisPort = mockk<JwtRedisPort>()
    val jwtAuthorizationExceptionFilter = mockk<JwtAuthorizationExceptionFilter>()
    val customJwtAuthorizationFilter = CustomJwtAuthorizationFilter(jwtService, memberUseCase, userDetailsServiceImpl, jwtRedisPort)

    given("a CustomJwtAuthorizationFilter") {

        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>()
        val filterChain = mockk<FilterChain>()

        When("Authorization header is missing") {
            every { jwtService.checkValidAccessHeader(request) } returns false
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, Jwt.HEADER_EXCEPTION, "Authorization 헤더가 누락되었습니다." ) } just runs
            every { response.status } returns HttpStatus.BAD_REQUEST.value()

            customJwtAuthorizationFilter.filter(request, response, filterChain)

            Then("it should return 400 Bad Request") {
                response.status shouldBe HttpStatus.BAD_REQUEST.value()
                verify(exactly = 1) {
                    jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, Jwt.HEADER_EXCEPTION, "Authorization 헤더가 누락되었습니다.")
                }
            }
        }

        When("accessToken is invalid") {
            // Test scenario for invalid accessToken
            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.onlyAccessToken(request) } returns true
            every { jwtService.extractAccessToken(request) } returns "INVALID_ACCESS_TOKEN"
            every { jwtService.checkValidToken(any(), any()) } returns false
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

        When("accessToken is expired") {
            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.onlyAccessToken(request) } returns true
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.checkValidToken(any(), any()) } returns true
            every { jwtService.isTokenExpired(any()) } returns true
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe Jwt.EXPIRED_EXCEPTION
                    this.message shouldBe "만료된 accessToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("accessToken has logout") {
            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.onlyAccessToken(request) } returns true
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.checkValidToken(any(), any()) } returns true
            every { jwtService.isTokenExpired(any()) } returns false
            every { jwtRedisPort.hasLogout(any()) } returns true
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe Jwt.EXPIRED_EXCEPTION
                    this.message shouldBe "만료된 accessToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("accessToken is expired after check logout") {
            val ex = mockk<ExpiredJwtException>()

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns false
            every { jwtService.onlyAccessToken(request) } returns true
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.checkValidToken(any(), Jwt.ACCESS) } returns true
            every { jwtService.isTokenExpired(any()) } returns false
            every { jwtRedisPort.hasLogout(any()) } returns true
            every { jwtService.extractClaims(any()) } throws ex
            every { ex.claims.subject } returns Jwt.ACCESS
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe Jwt.EXPIRED_EXCEPTION
                    this.message shouldBe "만료된 accessToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("refreshToken is invalid") {

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns true
            every { jwtService.onlyAccessToken(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.extractRefreshToken(request) } returns "INVALID_REFRESH_TOKEN"
            every { jwtService.checkValidToken(any(), Jwt.ACCESS) } returns true
            every { jwtService.checkValidToken(any(), Jwt.REFRESH) } returns false
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.JWT_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.JWT_EXCEPTION, "잘못된 refreshToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.JWT_EXCEPTION, "잘못된 refreshToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.code shouldBe Jwt.JWT_EXCEPTION
                    this.message shouldBe "잘못된 refreshToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("refreshToken is expired") {
            val ex = mockk<ExpiredJwtException>()

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns true
            every { jwtService.onlyAccessToken(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.extractRefreshToken(request) } returns "VALID_REFRESH_TOKEN"
            every { jwtService.checkValidToken(any(), Jwt.ACCESS) } returns true
            every { jwtService.checkValidToken(any(), Jwt.REFRESH) } returns true
            every { jwtService.extractClaims(any()) } throws  ex
            every { ex.claims.subject } returns Jwt.REFRESH
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "만료된 refreshToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<ExpiredJwtException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.message shouldBe "만료된 refreshToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("refreshToken is not in DB") {
            val id = 1
            val email = "test"
            val claim = mockk<Claims>()

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns true
            every { jwtService.onlyAccessToken(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.extractRefreshToken(request) } returns "VALID_REFRESH_TOKEN"
            every { jwtService.checkValidToken(any(), Jwt.ACCESS) } returns true
            every { jwtService.checkValidToken(any(), Jwt.REFRESH) } returns true
            every { jwtService.extractClaims(any()) } returns claim
            every { claim.get("id", String::class.java) } returns id.toString()
            every { claim.get("email", String::class.java)} returns email
            every { jwtRedisPort.findRefreshTokenByEmail(any()) } returns null
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "만료된 refreshToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.message shouldBe "만료된 refreshToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("refreshToken is not match DB") {
            val id = 1
            val email = "test"
            val claim = mockk<Claims>()

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns true
            every { jwtService.onlyAccessToken(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.extractRefreshToken(request) } returns "VALID_REFRESH_TOKEN"
            every { jwtService.checkValidToken(any(), Jwt.ACCESS) } returns true
            every { jwtService.checkValidToken(any(), Jwt.REFRESH) } returns true
            every { jwtService.extractClaims(any()) } returns claim
            every { claim.get("id", String::class.java) } returns id.toString()
            every { claim.get("email", String::class.java)} returns email
            every { jwtRedisPort.findRefreshTokenByEmail(any()) } returns "NOT_MATCH_HEADER_REFRESH_TOKEN"
            every {
                jwtService.compareRefreshToken(
                    "NOT_MATCH_HEADER_REFRESH_TOKEN",
                    "VALID_REFRESH_TOKEN",
                    email
                )
            } returns false
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, CustomJwtAuthorizationFilterException(Jwt.EXPIRED_EXCEPTION, "만료된 accessToken 입니다.")) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, "만료된 refreshToken 입니다." ) } just runs
            every { response.status } returns HttpStatus.UNAUTHORIZED.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<CustomJwtAuthorizationFilterException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.message shouldBe "만료된 refreshToken 입니다."
                }

                response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        When("member not found") {
            val id = 1
            val email = "test"
            val claim = mockk<Claims>()
            val ex = mockk<MemberDataNotFoundException>()

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns true
            every { jwtService.onlyAccessToken(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.extractRefreshToken(request) } returns "VALID_REFRESH_TOKEN"
            every { jwtService.checkValidToken(any(), Jwt.ACCESS) } returns true
            every { jwtService.checkValidToken(any(), Jwt.REFRESH) } returns true
            every { jwtService.extractClaims(any()) } returns claim
            every { claim.get("id", String::class.java) } returns id.toString()
            every { claim.get("email", String::class.java)} returns email
            every { jwtRedisPort.findRefreshTokenByEmail(any()) } returns "NOT_MATCH_HEADER_REFRESH_TOKEN"
            every {
                jwtService.compareRefreshToken(
                    "NOT_MATCH_HEADER_REFRESH_TOKEN",
                    "VALID_REFRESH_TOKEN",
                    email
                )
            } returns true
            every { memberUseCase.readMember(MemberUseCase.Commend.ReadCommend(id, email)) } throws ex
            every { filterChain.doFilter(request, response) } just runs
            every { jwtAuthorizationExceptionFilter.filter(request, response, filterChain) } just runs
            every { ex.message } returns "사용자가 존재하지 않습니다."
            every { ex.code.name } returns ErrorCode.DATA_NOT_FOUND.name
            every { jwtAuthorizationExceptionFilter.setErrorResponse(response, HttpStatus.BAD_REQUEST, ErrorCode.DATA_NOT_FOUND.name, ex) }
            every { jwtService.setErrorResponseMessage(response, HttpStatus.BAD_REQUEST, ErrorCode.DATA_NOT_FOUND.name, "사용자가 존재하지 않습니다." ) } just runs
            every { response.status } returns HttpStatus.BAD_REQUEST.value()

            Then("it should return 401 Unauthorized") {
                shouldThrowUnit<MemberDataNotFoundException> {
                    customJwtAuthorizationFilter.filter(request, response, filterChain)
                }.apply {
                    this.message shouldBe "사용자가 존재하지 않습니다."
                }

                response.status shouldBe HttpStatus.BAD_REQUEST.value()
            }
        }

        When("refreshToken is expireIn7Day") {
            val id = 1
            val email = "test"
            val claim = mockk<Claims>()
            val claim2 = mockk<Claims>()
            val member = mockk<Member>()
            val date = mockk<Date>()

            every { jwtService.checkValidAccessHeader(request) } returns true
            every { jwtService.checkValidRefreshHeader(request) } returns true
            every { jwtService.onlyAccessToken(request) } returns false
            every { jwtService.extractAccessToken(request) } returns "VALID_ACCESS_TOKEN"
            every { jwtService.extractRefreshToken(request) } returns "VALID_REFRESH_TOKEN"
            every { jwtService.checkValidToken("VALID_ACCESS_TOKEN", Jwt.ACCESS) } returns true
            every { jwtService.checkValidToken("VALID_REFRESH_TOKEN", Jwt.REFRESH) } returns true
            every { jwtService.extractClaims("VALID_REFRESH_TOKEN") } returns claim
            every { claim.get("id", String::class.java) } returns id.toString()
            every { claim.get("email", String::class.java)} returns email
            every { jwtRedisPort.findRefreshTokenByEmail(email) } returns "VALID_REFRESH_TOKEN"
            every {
                jwtService.compareRefreshToken(
                    "VALID_REFRESH_TOKEN",
                    "VALID_REFRESH_TOKEN",
                    email
                )
            } returns true
            every { memberUseCase.readMember(MemberUseCase.Commend.ReadCommend(id, email)) } returns member
            every { claim.expiration } returns date
            every { jwtService.checkRefreshTokenExpireDate(7, claim.expiration) } returns true
//            justRun { customJwtAuthorizationFilter.reissueRefreshToken(member, response) }


            Then("reissue RefreshToken") {
                every { jwtService.createRefreshToken(member) } returns "NEW_REFRESH_TOKEN"
                every { jwtService.extractClaims("NEW_REFRESH_TOKEN") } returns claim2
                every { claim2.expiration.time } returns 2L
                every { member.email } returns "test"
                justRun { jwtRedisPort.saveRefreshToken(any(), "NEW_REFRESH_TOKEN", 2L) }
                justRun { jwtService.setHeaderOfRefreshToken(response, "NEW_REFRESH_TOKEN") }
                every { jwtService.createAccessToken(member) } returns "NEW_ACCESS_TOKEN"
                justRun { jwtService.setHeaderOfAccessToken(response, "NEW_ACCESS_TOKEN") }
                every { member.role } returns "ROLE_ADMIN"
                justRun { filterChain.doFilter(request, response) }

                customJwtAuthorizationFilter.filter(request, response, filterChain)
                verify(exactly = 1) { filterChain.doFilter(request, response) }
            }
        }

        // Add more scenarios as needed
//        afterContainer {
//            clearAllMocks()
//        }
//
//        afterSpec {
//            clearAllMocks()
//        }

    }
})
