package com.example.spring.application.service.member

import com.example.spring.adapter.rest.member.mapper.MemberRestMapper
import com.example.spring.application.port.out.member.JwtRedisPort
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.exception.JwtRenewException
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.dto.JwtExceptionResponse
import com.example.spring.config.dto.LoginSuccessResponse
import com.example.spring.config.dto.LogoutSuccessResponse
import com.example.spring.domain.member.Jwt
import com.example.spring.domain.member.Jwt.Companion.ACCESS_TOKEN_HEADER
import com.example.spring.domain.member.Jwt.Companion.EXPIRED_EXCEPTION
import com.example.spring.domain.member.Jwt.Companion.JWT_EXCEPTION
import com.example.spring.domain.member.Jwt.Companion.REFRESH_TOKEN_HEADER
import com.example.spring.domain.member.Member
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce.Cluster.Refresh
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService(
    private val objectMapper: ObjectMapper,
    private val jwtRedisPort: JwtRedisPort,
    private val memberJpaPort: MemberJpaPort
) : Jwt {

    @Value("\${jwt.secret-key}")
    private val secretKey: String = ""

    @Value("\${jwt.access-time}")
    private val accessTime: Int = 0

    @Value("\${jwt.refresh-time}")
    private val refreshTime: Int = 0

    private val memberRestMapper = MemberRestMapper.INSTANCE

    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

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
        val claims: Claims = Jwts.claims().setSubject(Jwt.ACCESS)
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

    override fun createRefreshToken(member: Member): String {
        val tokenValidTime = getRefreshTime() * 60 * 1000L
        val now = Date()
        val refreshValidTime = Date(now.time + tokenValidTime)
        val claims: Claims = Jwts.claims().setSubject(Jwt.REFRESH)
        claims["email"] = member.email

        val token = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setSubject(Jwt.REFRESH)
            .setClaims(claims)
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
        extractClaims(token).get("email", String::class.java)

    /**
     * 인증정보 만들기
     * */
    override fun getAuthentication(email: String): UsernamePasswordAuthenticationToken {
        val member = memberJpaPort.findMemberByEmail(email)
        val userDetails = UserDetailsImpl(member)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    override fun setHeaderOfAccessToken(response: HttpServletResponse, token: String) {
        response.addHeader(Jwt.ACCESS_TOKEN_HEADER, Jwt.TOKEN_PREFIX + token)
    }

    override fun setHeaderOfRefreshToken(response: HttpServletResponse, token: String) {
        response.addHeader(Jwt.REFRESH_TOKEN_HEADER, Jwt.TOKEN_PREFIX + token)
    }

    override fun setResponseMessage(result: Boolean, response: HttpServletResponse, member: Member) {
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                LoginSuccessResponse(
                    result,
                    memberRestMapper.toMemberCommonResponse(member)
                )
            )
        )
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

    override fun checkValidToken(token: String, type: String): Boolean {
        return try {
            val claims = extractClaims(token)
            claims.subject == type
        } catch (e: ExpiredJwtException) {
            true
        } catch (e: JwtException) {
            false
        }
    }

    override fun checkValidAccessHeader(request: HttpServletRequest): Boolean {
        request.getHeader(Jwt.ACCESS_TOKEN_HEADER)
            ?.let { return it.startsWith(Jwt.TOKEN_PREFIX) }
            ?: return false

    }

    override fun checkValidRefreshHeader(request: HttpServletRequest): Boolean {
        request.getHeader(Jwt.REFRESH_TOKEN_HEADER)
            ?.let { return it.startsWith(Jwt.TOKEN_PREFIX) }
            ?: return false
    }

    override fun compareRefreshToken(
        refreshTokenFromDB: String,
        refreshTokenFromHeader: String,
        email: String
    ): Boolean =
        if (refreshTokenFromDB == refreshTokenFromHeader) {
            true
        } else {
            jwtRedisPort.deleteRefreshTokenByEmail(email)
            throw ExpiredJwtException(null, null, "토큰이 만료되었습니다.")
        }

    override fun checkRefreshTokenExpireDate(date: Int, expiration: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, date)
        val sevenDaysFromNow = calendar.time

        return expiration.before(sevenDaysFromNow)
    }

    override fun hasLogout(accessToken: String): Boolean =
        jwtRedisPort.hasLogout(accessToken)

    override fun saveLogoutToken(accessToken: String, expiration: Long) {
        val remainExpirationTime: Long = getRemainExpirationTime(expiration)
        jwtRedisPort.saveLogoutToken(accessToken, remainExpirationTime)
    }

    override fun saveRefreshToken(email: String, refreshToken: String, expiration: Long) {
        val remainExpirationTime: Long = getRemainExpirationTime(expiration)
        jwtRedisPort.saveRefreshToken(email, refreshToken, remainExpirationTime)
    }

    override fun findRefreshTokenByEmail(email: String): String? =
        jwtRedisPort.findRefreshTokenByEmail(email)

    override fun deleteRefreshTokenByEmail(email: String) =
        jwtRedisPort.deleteRefreshTokenByEmail(email)

    override fun getRemainExpirationTime(expiration: Long): Long =
        expiration - Date().time

    override fun deleteLogoutToken(accessToken: String) =
        jwtRedisPort.deleteLogoutToken(accessToken)

    override fun renewToken(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            if (checkValidAccessHeader(request).not())
                throw JwtRenewException(HttpStatus.BAD_REQUEST, ErrorCode.HEADER_EXCEPTION, "$ACCESS_TOKEN_HEADER 헤더가 누락되었습니다." )

            if (!checkValidRefreshHeader(request))
                throw JwtRenewException(HttpStatus.BAD_REQUEST, ErrorCode.HEADER_EXCEPTION, "$REFRESH_TOKEN_HEADER 헤더가 누락되었습니다." )

            // 토큰 추출
            val accessToken = extractAccessToken(request)
            val refreshToken = extractRefreshToken(request)

            // 토큰 검증
            if (!checkValidToken(accessToken, Jwt.ACCESS))
                throw JwtRenewException(HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION, "잘못된 accessToken 입니다.")

            if (!checkValidToken(refreshToken, Jwt.REFRESH))
                throw JwtRenewException(HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION, "잘못된 refreshToken 입니다.")

            val claim = extractClaims(refreshToken)
            val email = claim.get("email", String::class.java)

            // DB에서 refreshToken 존재 여부 검사
            val existRefreshToken: String = jwtRedisPort.findRefreshTokenByEmail(email)
                ?: throw JwtRenewException(HttpStatus.UNAUTHORIZED, ErrorCode.REFRESH_TOKEN_EXPIRED, "만료된 refreshToken 입니다.")

            // DB의 토큰과 Header의 refreshToken 일치 여부 검사
            if (!compareRefreshToken(existRefreshToken, refreshToken, email))
                throw JwtRenewException(HttpStatus.UNAUTHORIZED, ErrorCode.REFRESH_TOKEN_EXPIRED, "만료된 refreshToken 입니다.")

            val member: Member = memberJpaPort.findMemberByEmail(email)

            val expireIn7Day = checkRefreshTokenExpireDate(7, claim.expiration)
            if (expireIn7Day) reissueRefreshToken(member, response)

            reissueAccessToken(member, response)
        } catch (ex: JwtRenewException) {
            throw ex
        } catch (ex: ExpiredJwtException) {
            throw JwtRenewException(HttpStatus.UNAUTHORIZED, ErrorCode.REFRESH_TOKEN_EXPIRED, "만료된 refreshToken 입니다.")
        } catch (ex: JwtException) {
            throw JwtRenewException(HttpStatus.UNAUTHORIZED, ErrorCode.JWT_EXCEPTION, ex.message.toString())
        } catch (ex: NullPointerException) {
            throw JwtRenewException(HttpStatus.BAD_REQUEST, ErrorCode.NULL_POINTER, "토큰값이 비어있습니다.")
        } catch (ex: MemberDataNotFoundException) {
            throw ex
        } catch (ex: Exception) {
            log.warn("renew token unknown error")
            ex.printStackTrace()
            throw ex
        }
    }

    override fun setLogoutMessage(result: Boolean, response: HttpServletResponse) {
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                LogoutSuccessResponse(
                    result
                )
            )
        )
    }

    override fun checkAccessToken(accessToken: String) {
        check(checkValidToken(accessToken, Jwt.ACCESS)) { JWT_EXCEPTION + Jwt.ACCESS }
        check(isTokenExpired(accessToken).not()) { EXPIRED_EXCEPTION + Jwt.ACCESS }
        check(hasLogout(accessToken).not()) { EXPIRED_EXCEPTION + Jwt.ACCESS }
    }

    private fun reissueAccessToken(
        member: Member,
        response: HttpServletResponse
    ) {
        log.info("reissue AccessToken")
        val reissuedAccessToken = createAccessToken(member)
        setHeaderOfAccessToken(response, reissuedAccessToken)
    }

    private fun reissueRefreshToken(
        member: Member,
        response: HttpServletResponse
    ) {
        log.info("reissue RefreshToken")
        val reissuedRefreshToken = createRefreshToken(member)
        val expirationTime: Long = extractClaims(reissuedRefreshToken).expiration.time

        jwtRedisPort.saveRefreshToken(member.email, reissuedRefreshToken, expirationTime)
        setHeaderOfRefreshToken(response, reissuedRefreshToken)
    }

}