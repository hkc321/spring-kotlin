package com.example.spring.application.service.board

import com.example.spring.adapter.rest.board.dto.CommentReadChildCommentResponse
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class CommentService(private val commentJpaPort: CommentJpaPort) : CommentUseCase {
    override fun createComment(comment: Comment): Comment = commentJpaPort.createComment(comment)
    override fun readComment(commentId: Int): Comment = commentJpaPort.readComment(commentId)
    override fun readChildComment(commentId: Int, pageable: Pageable): CommentReadChildCommentResponse {
        val pageRequest: Pageable = PageRequest.of(
            pageable.pageNumber,
            20,
            Sort.by("commentId").ascending()
        )
        val sliceComment = commentJpaPort.readChildComment(commentId, pageRequest)
        return CommentReadChildCommentResponse(
            sliceComment.isEmpty,
            sliceComment.isLast,
            sliceComment.pageable.pageNumber,
            sliceComment.content
        )
    }

    override fun updateComment(commentId: Int, comment: Comment): Comment =
        commentJpaPort.updateComment(commentJpaPort.readComment(commentId).updateComment(comment))
}