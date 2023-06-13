package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.domain.board.Board
import org.springframework.stereotype.Service

@Service
class BoardService(private val boardJpaPort: BoardJpaPort) : BoardUseCase {
    override fun readAllBoard(): List<Board> = boardJpaPort.loadAllBoard()

    override fun readBoard(boardId: Int): Board = boardJpaPort.loadBoard(boardId)

    override fun writeBoard(board: Board): Board = boardJpaPort.saveBoard(board)

    override fun editBoard(board: Board, boardId: Int): Board = boardJpaPort.editBoard(board, boardId)

    override fun deleteBoard(boardId: Int) = boardJpaPort.deleteBoard(boardId)
}