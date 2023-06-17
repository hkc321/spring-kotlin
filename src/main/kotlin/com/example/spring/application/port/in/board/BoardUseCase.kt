package com.example.spring.application.port.`in`.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.BoardReadTopLevelCommentOnBoardResponse
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Pageable

interface BoardUseCase {
    /**
     * 게시글 전체 목록 조회
     * */
    fun readBoardList(boardReadBoardListRequest: BoardReadBoardListRequest): HashMap<String, Any>

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
    fun updateBoard(board: Board, boardId: Int): Board

    /**
     * 게시글 삭제
     * */
    fun deleteBoard(boardId: Int)

    /**
     * 게시글에 대한 댓글 검색
     * */
    fun readTopLevelCommentOnBoard(boardId: Int, pageable: Pageable): BoardReadTopLevelCommentOnBoardResponse
}