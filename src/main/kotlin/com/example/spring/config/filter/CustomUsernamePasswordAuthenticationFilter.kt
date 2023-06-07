package com.example.spring.config.filter

import com.example.spring.config.dto.ErrorCode
import com.example.spring.member.adapter.MemberRequest
import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.application.service.JwtService
import com.example.spring.member.application.service.UserDetailsImpl
import com.example.spring.member.domain.Member
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
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class CustomUsernamePasswordAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val memberPort: MemberPort
) : UsernamePasswordAuthenticationFilter() {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        log.info("try authentication")
        try {
            val om = ObjectMapper()
            val loginInput = om.readValue(request.inputStream, MemberRequest::class.java)
            val authentication = UsernamePasswordAuthenticationToken(loginInput.id, loginInput.pw)
            return authenticationManager.authenticate(authentication)
        } catch (e: Exception){
            e.printStackTrace()
            throw UsernameNotFoundException("Bad Credential")
        }
    }

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

        memberPort.saveRefreshToken(principal.username, refreshToken)

        jwtService.setHeaderOfAccessToken(response, accessToken)
        jwtService.setHeaderOfRefreshToken(response, refreshToken)

        jwtService.setResponseMessage(true, response, authResult.toString())
    }

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
        jwtService.setResponseMessage(false, response, "로그인 실패: $failMessage")
    }
}