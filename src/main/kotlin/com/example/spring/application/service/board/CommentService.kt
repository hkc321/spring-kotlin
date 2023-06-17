package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.domain.board.Comment
import org.springframework.stereotype.Service


@Service
class CommentService(private val commentJpaPort: CommentJpaPort) : CommentUseCase {
    override fun createComment(comment: Comment): Comment = commentJpaPort.createComment(comment)
    override fun readComment(commentId: Int): Comment = commentJpaPort.readComment(commentId)
}