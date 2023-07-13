package com.example.spring.application.service.member.exception

import com.example.spring.config.code.ErrorCode

data class MemberAlreadyExistException(
    val code: ErrorCode = ErrorCode.ALREADY_EXIST,
    override var message: String = "이미 존재하는 아이디입니다."
) : RuntimeException(message, null)
