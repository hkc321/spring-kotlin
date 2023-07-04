package com.example.spring.adapter.rest.board.dto

data class BoardCommonResponse(
    val boardId: Int,
    val name: String,
    val description: String,
    val writer: String,
    val createdAt: String,
    val modifier: String?,
    val updatedAt: String?
)
