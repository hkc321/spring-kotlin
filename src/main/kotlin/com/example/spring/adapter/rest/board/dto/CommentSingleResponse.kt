package com.example.spring.adapter.rest.board.dto

data class CommentSingleResponse(
    val boardId: Int,
    val postId: Int,
    val commentId: Int,
    val parentCommentId: Int?,
    val level: Int,
    val like: Int,
    val isLiked: Boolean,
    val content: String,
    val childCommentCount: Int,
    val writer: String,
    val createdAt: String,
    val updatedAt: String?
)
