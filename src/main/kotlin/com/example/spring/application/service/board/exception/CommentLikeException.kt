package com.example.spring.application.service.board.exception

import com.example.spring.config.code.ErrorCode

data class CommentLikeException(
    val boardId: Int,
    val postId: Int,
    val commentId: Int,
    val code: ErrorCode,
    override var message: String
) : RuntimeException(message, null)
