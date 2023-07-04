package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BoardJpaRepository: JpaRepository<BoardJpaEntity, Int> {
    fun findByBoardId(boardId: Int): BoardJpaEntity?
}