package com.example.spring.application.service.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.BoardReadTopLevelCommentOnBoardResponse
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BoardService(private val boardJpaPort: BoardJpaPort) : BoardUseCase {
    override fun readBoardList(boardReadBoardListRequest: BoardReadBoardListRequest): HashMap<String, Any> =
        boardJpaPort.loadAllBoard(boardReadBoardListRequest)

    override fun readBoard(boardId: Int): Board = boardJpaPort.loadBoard(boardId)

    override fun writeBoard(board: Board): Board = boardJpaPort.saveBoard(board)

    override fun editBoard(board: Board, boardId: Int): Board = boardJpaPort.editBoard(board, boardId)

    override fun deleteBoard(boardId: Int) = boardJpaPort.deleteBoard(boardId)
    override fun readTopLevelCommentOnBoard(boardId: Int, pageable: Pageable): BoardReadTopLevelCommentOnBoardResponse {
        val pageComment = boardJpaPort.readTopLevelCommentOnBoard(boardId, pageable)
        return BoardReadTopLevelCommentOnBoardResponse(
            pageComment.isEmpty,
            pageComment.isLast,
            pageComment.totalElements.toInt(),
            pageComment.pageable.pageNumber,
            pageComment.content
        )
    }
}