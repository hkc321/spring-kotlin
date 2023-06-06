package com.example.spring.member.domain

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

interface Jwt {
    /**
     * JWT Access Token 생성
     * */
    fun createAccessToken(authentication: Authentication): String

    /**
     * JWT Refresh Token 생성
     * */
    fun createRefreshToken(authentication: Authentication): String

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
     * Jwt Access Token subject(memIdx) 가져오기
     * */
    fun extractId(token: String): String

    /**
     * 인증정보 만들기
     * */
    fun getAuthentication(id: String): UsernamePasswordAuthenticationToken

}