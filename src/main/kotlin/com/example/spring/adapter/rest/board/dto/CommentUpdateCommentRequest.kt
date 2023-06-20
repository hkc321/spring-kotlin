package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Comment

data class CommentUpdateCommentRequest(
    var content: String,
) {
    init {
        require(content.isNotBlank())
    }
    fun toDomain(): Comment {
        val comment = Comment()
        comment.content = this.content

        return comment
    }
}
