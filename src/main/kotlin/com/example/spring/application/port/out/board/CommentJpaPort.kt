package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface CommentJpaPort {
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
    fun readChildComment(commentId: Int, pageable: Pageable): Slice<Comment>

    /**
     * 댓글 수정
     * */
    fun updateComment(comment: Comment): Comment

    /**
     * 게시글에 대한 댓글 검색
     * */
    fun readTopLevelCommentOnBoard(boardId: Int, pageable: Pageable): Page<Comment>

    /**
     * 대댓글 갯수 검색
     * */
    fun countChildComment(parentCommentId: Int, commentId: Int): Int
}