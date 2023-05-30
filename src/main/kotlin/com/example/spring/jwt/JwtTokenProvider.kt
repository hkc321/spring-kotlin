package com.example.spring.jwt

import com.example.spring.auth.model.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}")
    private val  secretKey: String,
    @Value("\${jwt.access-time}")
    private val  accessTime: Int,
    @Value("\${jwt.refresh-time}")
    private val  refreshTime: Int,
) {

    private lateinit var signingKey: SecretKey

    @PostConstruct
    private fun initializeSigningKey() {
        val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
        signingKey = Keys.hmacShaKeyFor(keyBytes)
    }


    fun createAccessToken(member: Member): String {
        val tokenValidTime = accessTime * 60 * 1000L
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
            .signWith(signingKey, SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    fun createRefreshToken(member: Member): String {
        val tokenValidTime = refreshTime * 60 * 1000L
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
            .signWith(signingKey, SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    fun resolveToken(req: HttpServletRequest): String? {
        return req.getHeader("X-AUTH-TOKEN")
    }

}