package com.example.spring.domain.member

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.*

interface Jwt {
    /**
     * JWT Access Token 생성
     * */
    fun createAccessToken(member: Member): String

    /**
     * JWT Refresh Token 생성
     * */
    fun createRefreshToken(member: Member): String

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
     * Jwt Access Token subject(email) 가져오기
     * */
    fun extractEmail(token: String): String

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

    /**
     * RefreshToken 탈취 검사
     * 해커가 먼저 토큰을 탈취하여 RefreshToken을 재발급 했을 시를 대비하여 DB의 RefreshToken과 Header의 RefreshToken을 비교
     * */
    fun compareRefreshToken(refreshTokenFromDB: String, refreshTokenFromHeader: String, email: String): Boolean

    /**
     * RefreshToken 만료일이 며칠 이내인지 검사
     * */
    fun checkRefreshTokenExpireDate(date: Int, expiration: Date): Boolean

    companion object {
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
}