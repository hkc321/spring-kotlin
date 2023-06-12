package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Board

interface BoardJpaPort {
    /**
     * 게시글 전체 조회
     * */
    fun getAllBoard(): List<Board>

    /**
     * 게시글 상세 조회
     * */
    fun getDetail(boardId: Int): Board
}