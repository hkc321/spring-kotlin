package com.example.spring.member.domain


import com.example.spring.member.domain.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

class RJwt {
    @Value("\${jwt.secret-key}")
    private val secretKey: String = ""

    @Value("\${jwt.access-time}")
    private val accessTime: Int = 0

    @Value("\${jwt.refresh-time}")
    private val refreshTime: Int = 0

    private fun getSingingKey(): SecretKey {
        val secretKey = getSecretKey()
        val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    // Getters for the properties
    private fun getSecretKey(): String {
        return secretKey
    }

    private fun getAccessTime(): Int {
        return accessTime
    }

    private fun getRefreshTime(): Int {
        return refreshTime
    }

    fun createAccessToken(member: Member): String {
        val tokenValidTime = getAccessTime() * 60 * 1000L
        val now = Date()
        val accessValidTime = Date(now.time + tokenValidTime)
        val claims: Claims = Jwts.claims().setSubject(member.memIdx.toString())
        claims["memIdx"] = member.memIdx
        claims["memId"] = member.memId

        val token =  Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setClaims(claims) // 정보저장
            .setIssuedAt(now)  // 토큰 발행 시간 정보
            .setExpiration(accessValidTime) // 만료시간
            .signWith(getSingingKey(), SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    fun createRefreshToken(member: Member): String {
        val tokenValidTime = getRefreshTime() * 60 * 1000L
        val now = Date()
        val refreshValidTime = Date(now.time + tokenValidTime)
        val claims: Claims = Jwts.claims().setSubject(member.memIdx.toString())
        claims["memIdx"] = member.memIdx
        claims["memId"] = member.memId

        val token =  Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setClaims(claims) // 정보저장
            .setIssuedAt(now)  // 토큰 발행 시간 정보
            .setExpiration(refreshValidTime) // 만료시간
            .signWith(getSingingKey(), SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    fun resolveToken(req: HttpServletRequest): String? {
        return req.getHeader("X-AUTH-TOKEN")
    }
}