package com.example.spring.adapter.rest.board.dto

data class CommentCreateRequest(
    val parentCommentId: Int? = null,
    val level: Int,
    val content: String
)
