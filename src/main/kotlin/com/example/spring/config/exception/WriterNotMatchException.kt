package com.example.spring.config.exception

import com.example.spring.config.code.ErrorCode

data class WriterNotMatchException(
    val code: ErrorCode = ErrorCode.INVALID_USER,
    override var message: String = "작성자만 수정이 가능합니다."
) : RuntimeException(message, null)
