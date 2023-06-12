package com.example.spring.adapter.rest.board.dto

import com.example.spring.config.dto.BaseResponseDto
import com.example.spring.domain.board.Board

class BoardResponse : BaseResponseDto() {
    lateinit var data: Board
}