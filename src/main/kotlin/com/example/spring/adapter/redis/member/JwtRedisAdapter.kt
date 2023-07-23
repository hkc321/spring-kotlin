package com.example.spring.adapter.redis.member

import com.example.spring.adapter.redis.member.repository.JwtRedisRepository
import com.example.spring.application.port.out.member.JwtRedisPort
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils


@Repository
class JwtRedisAdapter(private val jwtRedisRepository: JwtRedisRepository) : JwtRedisPort {
    override fun hasLogout(accessToken: String): Boolean =
        !ObjectUtils.isEmpty(jwtRedisRepository.findLogoutByToken(accessToken))

    override fun saveLogoutToken(accessToken: String, expiration: Long) =
        jwtRedisRepository.saveLogout(accessToken, expiration)

    override fun saveRefreshToken(email: String, refreshToken: String, expiration: Long) =
        jwtRedisRepository.saveRefreshToken(email, refreshToken, expiration)

    override fun findRefreshTokenByEmail(email: String): String? =
        jwtRedisRepository.findRefreshToken(email)

    override fun deleteRefreshTokenByEmail(email: String) =
        jwtRedisRepository.deleteRefreshToken(email)

    override fun deleteLogoutToken(accessToken: String) =
        jwtRedisRepository.deleteLogoutToken(accessToken)

}