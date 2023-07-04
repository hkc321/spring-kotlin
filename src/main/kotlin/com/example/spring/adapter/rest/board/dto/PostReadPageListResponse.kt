package com.example.spring.adapter.rest.board.dto

data class PostReadPageListResponse(
    val content: List<PostCommonResponse>?,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Int,
    val size: Int
)
