package com.example.spring.application.port.out.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.ReadTopLevelCommentOnBoardResponse
import com.example.spring.domain.board.Board
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
    fun editBoard(board: Board, boardId: Int): Board

    /**
     * 게시글 삭제
     * */
    fun deleteBoard(boardId: Int)

    /**
     * 게시글에 대한 댓글 검색
     * */
    fun readTopLevelCommentOnBoard(boardId: Int, pageable: Pageable): ReadTopLevelCommentOnBoardResponse
}