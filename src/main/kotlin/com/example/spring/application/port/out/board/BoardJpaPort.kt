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