package com.example.spring.application.service.member.exception

import com.example.spring.config.code.ErrorCode

data class MemberAccessorNotMatchException(
    val code: ErrorCode = ErrorCode.INVALID_USER,
    override var message: String = "본인의 정보만 접근 가능합니다."
) : RuntimeException(message, null)
