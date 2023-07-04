package com.example.spring.adapter.rest.board.dto

data class PostCommonResponse(
    val postId: Int,
    val boardId: Int,
    val title: String,
    val content: String,
    val up: Int,
    val writer: String,
    val createdAt: String,
    val updatedAt: String?
)
