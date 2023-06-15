package com.example.spring.application.service.board

import com.example.spring.adapter.rest.board.dto.CommentResponse
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class CommentService(private val commentJpaPort: CommentJpaPort) : CommentUseCase {
    override fun readBoardCommentTopLevelList(boardId: Int, pageable: Pageable): CommentResponse =
        commentJpaPort.readBoardCommentTopLevelList(boardId, pageable)
}