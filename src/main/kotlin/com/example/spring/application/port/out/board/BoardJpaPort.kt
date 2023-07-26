package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Board

interface BoardJpaPort {
    /**
     * 게시판 생성
     * */
    fun createBoard(board: Board): Board

    /**
     * 게시판 읽기
     * */
    fun readBoard(boardId: Int): Board

    /**
     * 게시판 수정
     * */
    fun updateBoard(board: Board): Board

    /**
     * 게시판 삭제
     * */
    fun deleteBoard(boardId: Int)

    /**
     * 게시판 제목으로 검색
     * */
    fun readBoardByName(name: String): Board?
}