package com.example.spring.application.service.board.exception

import com.example.spring.config.code.ErrorCode

data class BoardExistException(
    val code: ErrorCode = ErrorCode.ALREADY_EXIST,
    override var message: String
) : RuntimeException(message, null)
