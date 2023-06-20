package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Board
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BoardRequest(
    var title: String,
    var content: String,
    var writer: String
) {
    fun toDomain(): Board {
        val board = Board()
        board.title = this.title
        board.content = this.content
        board.writer = this.writer

        return board
    }
}