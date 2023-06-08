package com.example.spring.member.domain

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

interface Jwt {
    companion object{
        const val ACCESS = "AccessToken"
        const val REFRESH = "RefreshToken"
        const val EXCEPTION = "EXCEPTION"
        const val JWT_EXCEPTION = "JWT_EXCEPTION"
        const val HEADER_EXCEPTION = "HEADER_EXCEPTION"
        const val EXPIRED_EXCEPTION = "EXPIRED_EXCEPTION"
        const val ACCESS_TOKEN_HEADER = "Authorization"
        const val REFRESH_TOKEN_HEADER = "Authorization-refresh"
        const val TOKEN_PREFIX = "Bearer "
    }

    /**
     * JWT Access Token 생성
     * */
    fun createAccessToken(id: String): String

    /**
     * JWT Refresh Token 생성
     * */
    fun createRefreshToken(): String

    /**
     * Request Header에서 jwt token 가져오기
     * */
    fun resolveToken(req: HttpServletRequest): String?

    /**
     * JWT Access Token 유효성 확인
     * */
    fun validateToken(token: String): Boolean

    /**
     * JWT claim 추출
     * */
    fun extractClaims(token: String): Claims

    /**
     * Jwt Access Token 만료일자 확인
     * */
    fun isTokenExpired(token: String): Boolean

    /**
     * Jwt Access Token subject(id) 가져오기
     * */
    fun extractId(token: String): String

    /**
     * 인증정보 만들기
     * */
    fun getAuthentication(id: String): UsernamePasswordAuthenticationToken

    /**
     * JWT Access Token Header 설정
     * */
    fun setHeaderOfAccessToken(response: HttpServletResponse, token: String)

    /**
     * JWT Refresh Token Header 설정
     * */
    fun setHeaderOfRefreshToken(response: HttpServletResponse, token: String)

    /**
     * JWT 응답 메시지 설정
     * */
    fun setResponseMessage(result: Boolean, response: HttpServletResponse, message: String)

    /**
     * JWT 에러 메시지 설정
     * */
    fun setErrorResponseMessage(response: HttpServletResponse, status: HttpStatus, errorType: String, message: String)

    /**
     * Header에서 AccessToken 추출
     * */
    fun extractAccessToken(request: HttpServletRequest): String

    /**
     * Header에서 RefreshToken 추출
     * */
    fun extractRefreshToken(request: HttpServletRequest): String

    /**
     * Header에 AccessToken만 있는지 확인
     * RefreshToken도 같이 있으면 false 반환
     * */
    fun onlyAccessToken(request: HttpServletRequest): Boolean

    /**
     * 토큰 클래임 추출하여 유효한지 판단
     * */
    fun checkValidToken(token: String): Boolean

    /**
     * AccessToken Header 유효성 검사
     * 지정한 이름으로 왔는지 확인
     * */
    fun checkValidAccessHeader(request: HttpServletRequest): Boolean

    /**
     * RefreshToken Header 유효성 검사
     * 지정한 이름으로 왔는지 확인
     * */
    fun checkValidRefreshHeader(request: HttpServletRequest): Boolean
}