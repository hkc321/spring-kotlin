package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Comment

data class CommentRequest(
    var boardId: Int = -1,
    var parentCommentId: Int? = null,
    var level: Int = -1,
    var content: String,
    var writer: String,
) {
    init {
        require(content.isNotBlank())
        require(writer.isNotBlank())
        require(level > 0)
        require(boardId > 0)
    }
    fun toDomain(): Comment {
        val comment = Comment()
        comment.boardId = this.boardId
        comment.parentCommentId = this.parentCommentId
        comment.level = this.level
        comment.content = this.content
        comment.writer = this.writer

        return comment
    }
}
