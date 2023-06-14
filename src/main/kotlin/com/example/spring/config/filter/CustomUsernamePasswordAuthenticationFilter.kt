package com.example.spring.config.filter

import com.example.spring.config.dto.ErrorCode
import com.example.spring.adapter.rest.member.dto.MemberRequest
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsImpl
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
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
    private val memberJpaPort: MemberJpaPort
) : UsernamePasswordAuthenticationFilter() {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    /**
     * 로그인 시도
     * */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        log.info("try authentication")
        try {
            val om = ObjectMapper()
            val loginInput = om.readValue(request.inputStream, MemberRequest::class.java)
            val authentication = UsernamePasswordAuthenticationToken(loginInput.email, loginInput.pw)
            return authenticationManager.authenticate(authentication)
        } catch (e: Exception) {
            e.printStackTrace()
            throw UsernameNotFoundException("Bad Credential")
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

        val accessToken: String = jwtService.createAccessToken(principal.username)
        val refreshToken: String = jwtService.createRefreshToken()

        memberJpaPort.saveRefreshToken(principal.username, refreshToken)

        jwtService.setHeaderOfAccessToken(response, accessToken)
        jwtService.setHeaderOfRefreshToken(response, refreshToken)

        jwtService.setResponseMessage("true", response, "login success")
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
            ErrorCode.ITEM_NOT_EXIST.name -> ErrorCode.ITEM_NOT_EXIST.name
            ErrorCode.WRONG_PASSWORD.name -> ErrorCode.WRONG_PASSWORD.name
            else -> ErrorCode.UNKNOWN_ERROR.name
        }
        response.status = HttpStatus.BAD_REQUEST.value()
//        val failMessage = failed!!.message
        jwtService.setResponseMessage("false", response, "login fail: $failMessage")
    }
}