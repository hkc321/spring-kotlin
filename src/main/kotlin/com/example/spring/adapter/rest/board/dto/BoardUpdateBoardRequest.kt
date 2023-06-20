package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Board

data class BoardUpdateBoardRequest(
    var title: String,
    var content: String
)
{
    init {
        require(title.isNotBlank())
        require(content.isNotBlank())
    }
    fun toDomain(): Board {
        val board = Board()
        board.title = this.title
        board.content = this.content

        return board
    }
}
