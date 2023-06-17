package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Comment

interface CommentJpaPort {
    /**
     * 댓글 작성
     * */
    fun createComment(comment: Comment): Comment

    /**
     * 댓글 조회
     * */
    fun readComment(commentId: Int): Comment
}