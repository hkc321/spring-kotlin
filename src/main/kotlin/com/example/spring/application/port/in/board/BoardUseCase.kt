package com.example.spring.application.port.`in`.board

import com.example.spring.domain.board.Board

interface BoardUseCase {
    /**
     * 게시글 전체 목록 조회
     * */
    fun readAllBoard(): List<Board>

    /**
     * 게시글 상세 조회
     * */
    fun readBoard(boardId: Int): Board

    /**
     * 게시글 작성
     * */
    fun writeBoard(board: Board): Board

    /**
     * 게시글 수정
     * */
    fun editBoard(board: Board, boardId: Int): Board

    /**
     * 게시글 삭제
     * */
    fun deleteBoard(boardId: Int)
}