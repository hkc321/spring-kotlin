package com.example.spring.adapter.redis.member.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class JwtRedisRepository(private val redisTemplate: RedisTemplate<String, String>) {
    val valueOperation = redisTemplate.opsForValue()

    fun saveLogout(token: String, expiration: Long) {
        val key = "jwt:access:$token"
        valueOperation.set(key, "logout", expiration, TimeUnit.MILLISECONDS)
    }

    fun saveRefreshToken(email: String, token: String, expiration: Long) {
        val key = "jwt:refreshToken:$email"
        redisTemplate.opsForValue().getAndDelete(key)
        redisTemplate.opsForValue().set(key, token, expiration, TimeUnit.MILLISECONDS)
    }

    fun findLogoutByToken(token: String): String? {
        val key = "jwt:access:$token"
        return redisTemplate.opsForValue().get(key)
    }

    fun findRefreshToken(email: String): String? {
        val key = "jwt:refreshToken:$email"
        return redisTemplate.opsForValue().get(key)
    }

    fun deleteRefreshToken(email: String) {
        val key = "jwt:refreshToken:$email"
        valueOperation.getAndDelete(key)
    }

    fun deleteLogoutToken(token: String) {
        val key = "jwt:access:$token"
        valueOperation.getAndDelete(key)
    }

}