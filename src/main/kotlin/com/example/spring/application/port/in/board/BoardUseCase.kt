package com.example.spring.application.port.`in`.board

import com.example.spring.domain.board.Board

interface BoardUseCase {
    /**
     * 게시글 전체 목록 조회
     * */
    fun all(): List<Board>

    /**
     * 게시글 상세 조회
     * */
    fun detail(boardId: Int): Board

    /**
     * 게시글 작성
     * */
    fun write(board: Board): Board

    /**
     * 게시글 수정
     * */
    fun edit(board: Board, boardId: Int): Board

    /**
     * 게시글 삭제
     * */
    fun delete(boardId: Int)
}