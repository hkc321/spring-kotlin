package com.example.spring.application.service.member

import com.example.spring.config.filter.JwtExceptionResponse
import com.example.spring.domain.member.Jwt
import com.example.spring.domain.member.Member
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
    private val objectMapper: ObjectMapper
) : Jwt {

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

    override fun createAccessToken(member: Member): String {
        val tokenValidTime = getAccessTime() * 60 * 1000L
        val now = Date()
        val accessValidTime = Date(now.time + tokenValidTime)
        val claims: Claims = Jwts.claims().setSubject(member.memberId.toString())
        claims["email"] = member.email

        val token = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setSubject(Jwt.ACCESS)
            .setClaims(claims) // 정보저장
            .setIssuedAt(now)  // 토큰 발행 시간 정보
            .setExpiration(accessValidTime) // 만료시간
            .signWith(getSingingKey(), SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    override fun createRefreshToken(): String {
        val tokenValidTime = getRefreshTime() * 60 * 1000L
        val now = Date()
        val refreshValidTime = Date(now.time + tokenValidTime)

        val token = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setSubject(Jwt.REFRESH)
            .setIssuedAt(now)  // 토큰 발행 시간 정보
            .setExpiration(refreshValidTime) // 만료시간
            .signWith(getSingingKey(), SignatureAlgorithm.HS256) // signature에 들어갈 secret, 암호화 알고리즘
            .compact()

        return token
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        return req.getHeader(Jwt.ACCESS_TOKEN_HEADER).replace(Jwt.TOKEN_PREFIX, "")
    }

    override fun validateToken(token: String): Boolean {
        return try {
            extractClaims(token)
            true
        } catch (e: ExpiredJwtException) {
            true
        } catch (e: JwtException) {
            false
        }

//        val getMember = memberPort.findMemberById(extractEmail(token))
//        if (getMember != null) {
//            return !isTokenExpired(token)
//        }else{
//            return false
//        }
    }

    override fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSingingKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    override fun isTokenExpired(token: String): Boolean =
        extractClaims(token).expiration.before(Date())

    override fun extractEmail(token: String): String =
        extractClaims(token).toMap()["email"].toString()

    /**
     * 인증정보 만들기
     * */
    override fun getAuthentication(id: String): UsernamePasswordAuthenticationToken {
        val userDetails: UserDetails = userDetailsServiceImpl.loadUserByUsername(id)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    override fun setHeaderOfAccessToken(response: HttpServletResponse, token: String) {
        response.addHeader(Jwt.ACCESS_TOKEN_HEADER, Jwt.TOKEN_PREFIX + token)
    }

    override fun setHeaderOfRefreshToken(response: HttpServletResponse, token: String) {
        response.addHeader(Jwt.REFRESH_TOKEN_HEADER, Jwt.TOKEN_PREFIX + token)
    }

    override fun setResponseMessage(result: Boolean, response: HttpServletResponse, message: String) {
        response.contentType = "application/json;charset=UTF-8"
        val content = JSONObject()
            .apply { put("success", result) }
            .apply { put("message", message) }
        response
            .writer
            .print(content)
    }

    override fun setErrorResponseMessage(
        response: HttpServletResponse,
        status: HttpStatus,
        errorType: String,
        message: String
    ) {
        response.status = status.value()
        response.contentType = "application/json; charset=UTF-8"
        response.writer.write(objectMapper.writeValueAsString(JwtExceptionResponse(errorType, message)))
    }

    override fun extractAccessToken(request: HttpServletRequest): String {
        return request
            .getHeader(Jwt.ACCESS_TOKEN_HEADER)
            .replace(Jwt.TOKEN_PREFIX, "")
    }

    override fun extractRefreshToken(request: HttpServletRequest): String {
        return request
            .getHeader(Jwt.REFRESH_TOKEN_HEADER)
            .replace(Jwt.TOKEN_PREFIX, "")
    }

    override fun onlyAccessToken(request: HttpServletRequest): Boolean {
        return StringUtils.hasText(request.getHeader(Jwt.ACCESS_TOKEN_HEADER)) &&
                StringUtils.hasText(request.getHeader(Jwt.REFRESH_TOKEN_HEADER)).not()
    }

    override fun checkValidToken(token: String): Boolean {
        return try {
            extractClaims(token)
            true
        } catch (e: ExpiredJwtException) {
            true
        } catch (e: JwtException) {
            false
        }
    }

    override fun checkValidAccessHeader(request: HttpServletRequest): Boolean {
        request
            .apply { getHeader(Jwt.ACCESS_TOKEN_HEADER)?.startsWith(Jwt.TOKEN_PREFIX) ?: return false }
        return true
    }

    override fun checkValidRefreshHeader(request: HttpServletRequest): Boolean {
        request
            .apply { getHeader(Jwt.REFRESH_TOKEN_HEADER)?.startsWith(Jwt.TOKEN_PREFIX) ?: return false }
        return true
    }

}