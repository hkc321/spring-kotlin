package com.example.spring.adapter.rest.board.dto

data class CommentTopLevelResponse(
    val comments: List<CommentCommonResponse?>?,
    val nextCursor: Int?
)
