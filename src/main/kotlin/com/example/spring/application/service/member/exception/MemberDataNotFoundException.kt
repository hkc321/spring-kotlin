package com.example.spring.application.service.member.exception

import com.example.spring.config.code.ErrorCode

data class MemberDataNotFoundException(
    val code: ErrorCode = ErrorCode.DATA_NOT_FOUND,
    override var message: String = "사용자가 존재하지 않습니다."
) : RuntimeException(message, null)
