package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PostJpaRepository: JpaRepository<PostJpaEntity, Int> {
    fun findByBoardAndPostId(board: BoardJpaEntity, postInt: Int): PostJpaEntity?
}