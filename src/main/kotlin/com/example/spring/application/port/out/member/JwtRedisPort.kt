package com.example.spring.application.port.out.member

interface JwtRedisPort {
    /**
     * 로그아웃된 토근인지 확인
     * */
    fun hasLogout(accessToken: String): Boolean

    /**
     * 로그아웃 토큰 리스트에 등록
     * */
    fun saveLogoutToken(accessToken: String, expiration: Long)

    /**
     * 리프레시 토큰 저장
     * */
    fun saveRefreshToken(email: String, refreshToken: String, expiration: Long)

    /**
     * 리프레시 토큰 찾기
     * */
    fun findRefreshTokenByEmail(email: String): String?

    /**
     * 리프레시 토큰 제거
     * */
    fun deleteRefreshTokenByEmail(email: String)
}