package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentJpaRepository : JpaRepository<CommentJpaEntity, Int> {
    fun findByBoardAndPostAndLevel(boardJpaEntity: BoardJpaEntity, postJpaEntity: PostJpaEntity, level: Int)
    fun findByBoardAndPostAndCommentId(
        boardJpaEntity: BoardJpaEntity,
        postJpaEntity: PostJpaEntity,
        commentId: Int
    ): CommentJpaEntity?

    fun countByParentComment(commentJpaEntity: CommentJpaEntity): Int
}