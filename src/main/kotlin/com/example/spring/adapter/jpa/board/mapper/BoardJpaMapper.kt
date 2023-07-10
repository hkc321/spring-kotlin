package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.BoardKotlinJdslAdapter
import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.domain.board.Board
import org.springframework.stereotype.Repository

@Repository
class BoardJpaMapper(private val memberJpaRepository: MemberJpaRepository) {
    fun toBoard(boardJpaEntity: BoardJpaEntity): Board {
        return boardJpaEntity.let {
            Board(
                boardId = it.boardId,
                name = it.name,
                description = it.description,
                writer = it.writer.email,
                modifier = it.modifier.email
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }

    fun toBoard(boardJpaEntity: BoardKotlinJdslAdapter.BoardPageResult): Board {
        return boardJpaEntity.let {
            Board(
                boardId = it.boardId,
                name = it.name,
                description = it.description,
                writer = it.writer,
                modifier = it.modifier
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }

    fun toJpaEntity(board: Board): BoardJpaEntity {
        return board.let {
            BoardJpaEntity(
                boardId = it.boardId,
                name = it.name,
                description = it.description,
                writer = memberJpaRepository.findByEmail(it.writer)!!,
                modifier = memberJpaRepository.findByEmail(it.modifier)!!
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }
}