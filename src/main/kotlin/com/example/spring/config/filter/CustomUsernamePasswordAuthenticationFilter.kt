package com.example.spring.config.filter

import com.example.spring.adapter.rest.member.dto.MemberLoginRequest
import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsImpl
import com.example.spring.config.code.ErrorCode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * 로그인 시도 시 작동하는 필터(인증필터)
 * */
class CustomUsernamePasswordAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val memberUseCase: MemberUseCase
) : UsernamePasswordAuthenticationFilter() {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    /**
     * 로그인 시도
     * */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        log.info("try authentication")
        try {
            val om = ObjectMapper()
            val loginInput = om.readValue(request.inputStream, MemberLoginRequest::class.java)
            val authentication = UsernamePasswordAuthenticationToken(loginInput.email, loginInput.password)
            return authenticationManager.authenticate(authentication)
        } catch (ex: UnrecognizedPropertyException) {
            throw UsernameNotFoundException(ErrorCode.INVALID_PARAMETER.name)
        } catch (ex: InternalAuthenticationServiceException) {
            throw UsernameNotFoundException(ErrorCode.DATA_NOT_FOUND.name)
        } catch (ex: BadCredentialsException) {
            throw UsernameNotFoundException(ErrorCode.WRONG_PASSWORD.name)
        } catch (ex: MismatchedInputException) {
            throw UsernameNotFoundException(ErrorCode.EMPTY_INPUT.name)
        } catch (ex: Exception) {
            log.warn("login unknown error")
            ex.printStackTrace()
            throw UsernameNotFoundException(ErrorCode.INTERNAL_SERVER_ERROR.name)
        }
    }

    /**
     * 로그인 성공 시 작동
     * */
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        log.info("success authentication - provide JWT")
        val principal: UserDetailsImpl = authResult!!.principal as UserDetailsImpl

        val accessToken: String = jwtService.createAccessToken(principal.getMember())
        val refreshToken: String = jwtService.createRefreshToken()

        memberUseCase.saveRefreshToken(
            MemberUseCase.Commend.SaveRefreshTokenCommend(
                principal.username, refreshToken
            )
        )

        jwtService.setHeaderOfAccessToken(response, accessToken)
        jwtService.setHeaderOfRefreshToken(response, refreshToken)

        jwtService.setResponseMessage(true, response, "login success")
    }

    /**
     * 로그인 실패 시 작동
     * */
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        failed: AuthenticationException?
    ) {
        log.info("fail authentication")
        val failMessage = when (failed!!.message) {
            ErrorCode.DATA_NOT_FOUND.name -> "사용자가 존재하지 않습니다."
            ErrorCode.WRONG_PASSWORD.name -> "비밀번호가 올바르지 않습니다."
            ErrorCode.INVALID_PARAMETER.name -> "프로퍼티 이름이 올바르지 않습니다. required:[email, password]"
            ErrorCode.EMPTY_INPUT.name -> "빈 값이 전달되었습니다. request body를 확인해 주세요"
            else -> ErrorCode.UNKNOWN_ERROR.name
        }
        response.status = when (failed!!.message) {
            ErrorCode.INTERNAL_SERVER_ERROR.name -> HttpStatus.INTERNAL_SERVER_ERROR.value()
            else -> HttpStatus.BAD_REQUEST.value()
        }
        jwtService.setResponseMessage(false, response, "$failMessage")
    }
}