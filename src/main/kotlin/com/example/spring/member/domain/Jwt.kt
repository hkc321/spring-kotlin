package com.example.spring.member.domain

import jakarta.servlet.http.HttpServletRequest

interface Jwt {
    fun createAccessToken(member: Member): String
    fun createRefreshToken(member: Member): String
    fun resolveToken(req: HttpServletRequest): String?
}