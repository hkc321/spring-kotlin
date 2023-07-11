package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.port.out.board.BoardKotlinJdslPort
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardService(
    private val boardJpaPort: BoardJpaPort,
    private val boardKotlinJdslPort: BoardKotlinJdslPort
) : BoardUseCase {

    @Transactional
    override fun createBoard(commend: BoardUseCase.Commend.CreateCommend): Board =
        boardJpaPort.createBoard(
            Board(
                name = commend.name,
                description = commend.description,
                writer = commend.writer,
                modifier = commend.writer
            )
        )

    @Transactional(readOnly = true)
    override fun readBoardPageList(commend: BoardUseCase.Commend.ReadListCommend): Page<Board> =
        boardKotlinJdslPort.readBoardPageList(commend.keyword, commend.searchType, commend.pageable)

    @Transactional(readOnly = true)
    override fun readBoard(commend: BoardUseCase.Commend.ReadCommend): Board = boardKotlinJdslPort.readBoard(commend.boardId)

    @Transactional
    override fun updateBoard(commend: BoardUseCase.Commend.UpdateCommend): Board {
        val board: Board = boardKotlinJdslPort.readBoard(commend.boardId)
        board.update(
            name = commend.name,
            description = commend.description,
            modifier = commend.modifier
        )
        return boardJpaPort.updateBoard(board)
    }

    @Transactional
    override fun deleteBoard(commend: BoardUseCase.Commend.DeleteCommend) = boardJpaPort.deleteBoard(commend.boardId)
}