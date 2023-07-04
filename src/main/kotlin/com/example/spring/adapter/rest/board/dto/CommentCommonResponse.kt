package com.example.spring.adapter.rest.board.dto

data class CommentCommonResponse(
    val boardId: Int,
    val postId: Int,
    val commentId: Int,
    val parentCommentId: Int?,
    val level: Int,
    val content: String,
    val writer: String,
    val createdAt: String,
    val updatedAt: String?
)
