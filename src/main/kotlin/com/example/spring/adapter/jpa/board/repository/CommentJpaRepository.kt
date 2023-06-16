package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface CommentJpaRepository : JpaRepository<CommentJpaEntity, Int> {
    fun findSliceByBoardIdAndLevel(boardId: Int, pageable: Pageable, level: Int = 0): Slice<CommentJpaEntity>
    fun countByParentCommentIdAndCommentIdIsNot(parentCommentId: Int, commentId: Int): Int
}