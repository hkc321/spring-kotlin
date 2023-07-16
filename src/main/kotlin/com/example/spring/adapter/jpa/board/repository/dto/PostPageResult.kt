package com.example.spring.adapter.jpa.board.repository.dto

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import java.time.LocalDateTime

data class PostPageResult(
    val board: BoardJpaEntity,
    val postId: Int,
    val title: String,
    val content: String,
    val like: Int,
    val writer: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
