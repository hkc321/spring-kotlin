package com.example.spring.application.port.out.board

import com.example.spring.adapter.rest.board.dto.CommentCommonResponse
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Comment
import com.example.spring.domain.board.Post

interface CommentJpaPort {
    /**
     * 댓글 작성
     * */
    fun createComment(comment: Comment): Comment

    /**
     * 댓글 조회
     * */
    fun readComment(boardId: Int, postId: Int, commentId: Int): Comment

    /**
     * 댓글 수정
     * */
    fun updateComment(comment: Comment): Comment

    /**
     * 댓글 삭제
     * */
    fun deleteComment(boardId: Int, postId: Int, commentId: Int)
}