package com.example.spring.adapter.rest.board.dto

data class BoardCreateRequest(
    val name: String,
    val description: String,
    val writer: String
)
