package com.example.spring.application.service.member.exception

import com.example.spring.config.code.ErrorCode
import org.springframework.http.HttpStatus

data class JwtRenewException(
    val status: HttpStatus,
    val code: ErrorCode,
    override var message: String
) : RuntimeException(message, null)