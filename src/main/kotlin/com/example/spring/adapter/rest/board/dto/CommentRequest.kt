package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Comment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CommentRequest(
    var commentId: Int? = null,
    var boardId: Int,
    var parentCommentId: Int,
    var level: Int = 0,
    var content: String,
    var up: Int,
    var writer: String,
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    var updatedAt: String? = null
) {
    fun toDomain(): Comment {
        val comment = Comment()
        this.commentId?.apply { comment.commentId = this }
        comment.boardId = this.boardId
        comment.parentCommentId = this.parentCommentId
        comment.level = this.level
        comment.content = this.content
        comment.up = this.up
        comment.writer = this.writer
        comment.createdAt = this.createdAt
        comment.updatedAt = this.updatedAt

        return comment
    }
}
