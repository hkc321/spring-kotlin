package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Comment

data class CommentResponse(
    val commentList: List<Comment>,
    val islast: Boolean,
    val isEmpty: Boolean
)
