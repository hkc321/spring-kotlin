package com.example.spring.application.port.`in`.board

import com.example.spring.domain.board.Comment

interface CommentUseCase {
    /**
     * 댓글 작성
     * */
    fun createComment(comment: Comment): Comment

    /**
     * 댓글 조회
     * */
    fun readComment(commentId: Int): Comment
}