package com.example.spring.application.service.board.exception

import com.example.spring.config.code.ErrorCode

data class PostLikeException(
    val boardId: Int,
    val postId: Int,
    val code: ErrorCode,
    override var message: String
) : RuntimeException(message, null)