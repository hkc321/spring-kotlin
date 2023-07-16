package com.example.spring.adapter.jpa.board.repository.dto

import java.time.LocalDateTime

data class BoardResult(
    val boardId: Int,
    var name: String,
    var description: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var writer: String,
    var modifier: String,
)
