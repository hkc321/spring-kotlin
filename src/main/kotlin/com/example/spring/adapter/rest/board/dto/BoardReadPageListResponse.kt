package com.example.spring.adapter.rest.board.dto

data class BoardReadPageListResponse(
    val content: List<BoardCommonResponse>?,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Int,
    val size: Int
)
