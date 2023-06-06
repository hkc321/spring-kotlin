package com.example.spring.member.application.service

import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.domain.Jwt
import com.example.spring.member.domain.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val memberPort: MemberPort,
    private val userDetailsServiceImpl: UserDetailsServiceImpl): Jwt {

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

    override fun createAccessToken(authentication: Authentication): String {
        val tokenValidTime = getAccessTime() * 60 * 1000L
        val now = Date()
        val accessValidTime = Date(now.time + tokenValidTime)
        val claims: Claims = Jwts.claims().setSubject(authentication.name)
        claims["id"] = authentication.name

        val token =  Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setSubject(authentication.name)
            .setClaims(claims) // 정보저장
            .setIssuedAt(now)  // 토큰 발행 시간 정보
            .setExpiration(accessValidTime) // 만료시간
            .signWith(getSingingKey(), SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    override fun createRefreshToken(authentication: Authentication): String {
        val tokenValidTime = getRefreshTime() * 60 * 1000L
        val now = Date()
        val refreshValidTime = Date(now.time + tokenValidTime)
        val claims: Claims = Jwts.claims().setSubject(authentication.name)
        claims["id"] = authentication.name

        val token =  Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setSubject(authentication.name)
            .setClaims(claims) // 정보저장
            .setIssuedAt(now)  // 토큰 발행 시간 정보
            .setExpiration(refreshValidTime) // 만료시간
            .signWith(getSingingKey(), SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        return req.getHeader("Authorization")
    }

    override fun validateToken(token: String): Boolean {
        val getMember = memberPort.findMemberById(extractId(token))
        if (getMember != null) {
            return !isTokenExpired(token)
        }else{
            return false
        }
    }

    override fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSingingKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    override fun isTokenExpired(token: String): Boolean {
        return extractClaims(token).expiration.before(Date())
    }

    override fun extractId(token: String): String {
        return extractClaims(token).subject
    }

    /**
     * 인증정보 만들기
     * */
    override fun getAuthentication(id: String): UsernamePasswordAuthenticationToken {
        val userDetails: UserDetails = userDetailsServiceImpl.loadUserByUsername(id)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

}