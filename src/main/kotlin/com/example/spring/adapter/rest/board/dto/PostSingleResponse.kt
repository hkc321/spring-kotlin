package com.example.spring.adapter.rest.board.dto

data class PostSingleResponse(
    val postId: Int,
    val boardId: Int,
    val title: String,
    val content: String,
    val like: Int,
    val isLiked: Boolean,
    val writer: String,
    val createdAt: String,
    val updatedAt: String?
)
