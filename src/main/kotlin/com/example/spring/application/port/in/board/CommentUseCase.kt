package com.example.spring.application.port.`in`.board

import com.example.spring.adapter.rest.board.dto.CommentReadBoardCommentTopLevelListResponse
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Pageable

interface CommentUseCase {
    /**
     * 게시물에 대한 최상위 레벨 댓글 조회
     * */
    fun readBoardCommentTopLevelList(boardId: Int, pageable: Pageable): CommentReadBoardCommentTopLevelListResponse

    /**
     * 댓글 작성
     * */
    fun createComment(comment: Comment): Comment
}