package com.example.spring.application.service.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.BoardReadTopLevelCommentOnBoardResponse
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BoardService(private val boardJpaPort: BoardJpaPort, private val commentJpaPort: CommentJpaPort) : BoardUseCase {
    override fun readBoardList(boardReadBoardListRequest: BoardReadBoardListRequest): HashMap<String, Any> =
        boardJpaPort.loadAllBoard(boardReadBoardListRequest)

    override fun readBoard(boardId: Int): Board = boardJpaPort.loadBoard(boardId)

    override fun writeBoard(board: Board): Board = boardJpaPort.saveBoard(board)

    override fun updateBoard(board: Board, boardId: Int): Board = boardJpaPort.updateBoard(board, boardId)

    override fun deleteBoard(boardId: Int) = boardJpaPort.deleteBoard(boardId)
    override fun readTopLevelCommentOnBoard(boardId: Int, pageable: Pageable): BoardReadTopLevelCommentOnBoardResponse {
        val pageComment: Page<Comment> = commentJpaPort.readTopLevelCommentOnBoard(boardId, pageable)
        pageComment.apply {
            this.map {
                it.childCommentCount =
                    commentJpaPort.countChildComment(it.parentCommentId, it.commentId)
            }
            return BoardReadTopLevelCommentOnBoardResponse(
                this.isEmpty,
                this.isLast,
                this.totalElements.toInt(),
                this.pageable.pageNumber,
                this.content
            )
        }
    }
}