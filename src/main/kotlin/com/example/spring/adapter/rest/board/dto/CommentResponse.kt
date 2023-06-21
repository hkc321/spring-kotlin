package com.example.spring.adapter.rest.board.dto

data class CommentResponse(
    var commentId: Int,
    var boardId: Int,
    var parentCommentId: Int?,
    var level: Int,
    var content: String,
    var up: Int,
    var writer: String,
    var createdAt: String,
    var updatedAt: String?
)
