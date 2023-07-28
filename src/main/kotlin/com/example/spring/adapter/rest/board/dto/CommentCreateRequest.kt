package com.example.spring.adapter.rest.board.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CommentCreateRequest(
    val parentCommentId: Int? = null,
    @field: Min(value = 1, message = "level은 1 이상이여야 합니다.")
    @field: Max(value = 2, message = "level은 2 이하여야 합니다.")
    val level: Int,
    val content: String
)
