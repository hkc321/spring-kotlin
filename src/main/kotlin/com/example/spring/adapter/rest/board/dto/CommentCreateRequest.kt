package com.example.spring.adapter.rest.board.dto

import jakarta.validation.constraints.Min

data class CommentCreateRequest(
    val parentCommentId: Int? = null,
    @field: Min(value = 1, message = "level은 1 이상이여야 합니다.")
    val level: Int,
    val content: String
)
