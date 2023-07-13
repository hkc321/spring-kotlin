package com.example.spring.application.service.board.exception

import com.example.spring.config.code.ErrorCode

data class BoardDataNotFoundException(
    val boardId: Int,
    val code: ErrorCode = ErrorCode.DATA_NOT_FOUND,
    override var message: String = "게시판이 존재하지 않습니다. [boardId: $boardId]"
) : RuntimeException(message, null)
