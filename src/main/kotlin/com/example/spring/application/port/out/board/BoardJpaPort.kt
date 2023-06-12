package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Board

interface BoardJpaPort {
    /**
     * 게시글 전체 조회
     * */
    fun getAllBoard(): List<Board>
}