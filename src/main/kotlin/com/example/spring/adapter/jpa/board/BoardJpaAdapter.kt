package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class BoardJpaAdapter(private val boardJpaRepository: BoardJpaRepository) : BoardJpaPort {
    val boardJpaMapper = BoardJpaMapper.INSTANCE

    override fun getAllBoard(): List<Board> {
        val entities = boardJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        return entities.map { boardJpaMapper.toBoard(it) }
    }

    override fun getDetail(boardId: Int): Board = boardJpaMapper.toBoard(boardJpaRepository.findByBoardId(boardId))
}