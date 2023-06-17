package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Comment

data class CommentReadChildCommentResponse(
    val isEmpty: Boolean,
    val islast: Boolean,
    val pageNumber: Int,
    val commentList: List<Comment>
)
