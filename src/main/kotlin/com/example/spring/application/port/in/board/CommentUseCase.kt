package com.example.spring.application.port.`in`.board

import com.example.spring.adapter.rest.board.dto.CommentReadChildCommentResponse
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Pageable

interface CommentUseCase {
    /**
     * 댓글 작성
     * */
    fun createComment(comment: Comment): Comment

    /**
     * 댓글 조회
     * */
    fun readComment(commentId: Int): Comment

    /**
     * 대댓글 조회
     * */
    fun readChildComment(commentId: Int, pageable: Pageable): CommentReadChildCommentResponse
}