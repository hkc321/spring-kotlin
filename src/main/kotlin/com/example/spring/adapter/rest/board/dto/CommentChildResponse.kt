package com.example.spring.adapter.rest.board.dto

data class CommentChildResponse(
    val comments: List<CommentSingleResponse?>?,
    val nextCursor: Int?
)
