package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface CommentJpaRepository : JpaRepository<CommentJpaEntity, Int> {
    fun findPageByBoardIdAndLevel(boardId: Int, pageable: Pageable, level: Int = 0): Page<CommentJpaEntity>
    fun countByParentCommentIdAndCommentIdIsNot(parentCommentId: Int, commentId: Int): Int
}