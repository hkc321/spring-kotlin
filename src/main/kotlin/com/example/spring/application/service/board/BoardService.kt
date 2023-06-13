package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.domain.board.Board
import org.springframework.stereotype.Service

@Service
class BoardService(private val boardJpaPort: BoardJpaPort) : BoardUseCase {
    override fun all(): List<Board> = boardJpaPort.getAllBoard()

    override fun detail(boardId: Int): Board = boardJpaPort.getDetail(boardId)

    override fun write(board: Board): Board = boardJpaPort.write(board)

    override fun edit(board: Board, boardId: Int): Board = boardJpaPort.edit(board, boardId)

    override fun delete(boardId: Int) = boardJpaPort.delete(boardId)
}