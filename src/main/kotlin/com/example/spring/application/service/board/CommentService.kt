package com.example.spring.application.service.board

import com.example.spring.adapter.rest.board.dto.CommentReadBoardCommentTopLevelListResponse
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class CommentService(private val commentJpaPort: CommentJpaPort) : CommentUseCase {
    override fun readBoardCommentTopLevelList(
        boardId: Int,
        pageable: Pageable
    ): CommentReadBoardCommentTopLevelListResponse =
        commentJpaPort.readBoardCommentTopLevelList(boardId, pageable)

    override fun createComment(comment: Comment): Comment = commentJpaPort.createComment(comment)
    override fun readComment(commentId: Int): Comment = commentJpaPort.readComment(commentId)
}