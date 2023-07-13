package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.config.controller.BoardDataNotFoundException
import com.example.spring.domain.board.Board
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository


@Repository
class BoardJpaAdapter(
    private val boardJpaRepository: BoardJpaRepository,
    private val boardJpaMapper: BoardJpaMapper
) : BoardJpaPort {

    override fun createBoard(board: Board): Board =
        boardJpaMapper.toBoard(boardJpaRepository.save(boardJpaMapper.toJpaEntity(board)))

    override fun readBoard(boardId: Int): Board =
        boardJpaRepository.findByIdOrNull(boardId)
            ?.let {
                return boardJpaMapper.toBoard(it)
            }
            ?: throw BoardDataNotFoundException(boardId = boardId)

    override fun updateBoard(board: Board): Board =
        boardJpaMapper.toBoard(boardJpaRepository.save(boardJpaMapper.toJpaEntity(board)))

    override fun deleteBoard(boardId: Int) {
        boardJpaRepository.findByIdOrNull(boardId)
            ?.let {
                boardJpaRepository.deleteById(boardId)
            }
            ?: throw BoardDataNotFoundException(boardId = boardId)
    }
}