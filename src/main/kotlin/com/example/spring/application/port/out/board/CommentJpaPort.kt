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
    fun readComment(boardId: Int, postId: Int, commentId: Int): Comment?

    /**
     * 댓글 수정
     * */
    fun updateComment(comment: Comment): Comment

    /**
     * 댓글 삭제
     * */
    fun deleteComment(boardId: Int, postId: Int, commentId: Int)
}