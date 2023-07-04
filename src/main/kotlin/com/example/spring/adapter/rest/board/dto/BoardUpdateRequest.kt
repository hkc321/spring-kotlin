package com.example.spring.adapter.rest.board.dto

data class BoardUpdateRequest(
    val name: String,
    val description: String,
    val modifier: String
)
