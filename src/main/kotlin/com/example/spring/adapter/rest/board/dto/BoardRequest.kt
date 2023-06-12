package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Board
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardRequest {
    var title: String = ""
    var content: String = ""
    var up: Int = 0
    var writer: String = ""
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    var editedAt: String? = null

    fun toDomain(): Board {
        val board = Board()
        board.title = this.title
        board.content = this.content
        board.up = this.up
        board.writer = this.writer

        return board
    }
}