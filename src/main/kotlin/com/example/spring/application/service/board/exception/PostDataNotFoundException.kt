package com.example.spring.application.service.board.exception

import com.example.spring.config.code.ErrorCode

data class PostDataNotFoundException(
    val boardId: Int,
    val postId: Int,
    val code: ErrorCode = ErrorCode.DATA_NOT_FOUND,
    override var message: String = "게시글이 존재하지 않습니다. [boardId: $boardId, postId: $postId]"
) : RuntimeException(message, null)
