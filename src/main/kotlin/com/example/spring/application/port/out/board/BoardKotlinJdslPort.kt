package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BoardKotlinJdslPort {
    /**
     * 게시판 조회
     * */
    fun readBoard(boardId: Int): Board

    /**
     * 게시판 리스트 조회
     * */
    fun readBoardPageList(keyword: String?, searchType: String?, pageable: Pageable): Page<Board>
}