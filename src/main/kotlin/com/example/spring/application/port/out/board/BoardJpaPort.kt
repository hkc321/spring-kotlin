package com.example.spring.application.port.out.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BoardJpaPort {
    /**
     * 게시글 전체 조회
     * */
    fun loadAllBoard(boardReadBoardListRequest: BoardReadBoardListRequest): HashMap<String, Any>

    /**
     * 게시글 상세 조회
     * */
    fun loadBoard(boardId: Int): Board

    /**
     * 게시글 작성
     * */
    fun saveBoard(board: Board): Board

    /**
     * 게시글 수정
     * */
    fun updateBoard(board: Board): Board

    /**
     * 게시글 삭제
     * */
    fun deleteBoard(boardId: Int)

}