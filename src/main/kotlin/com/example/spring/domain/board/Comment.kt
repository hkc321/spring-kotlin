package com.example.spring.domain.board

import com.example.spring.config.domain.CommonDateDomain
import java.time.LocalDateTime

class Comment(
    commentId: Int = 0,
    boardId: Int,
    postId: Int,
    parentComment: Int?,
    level: Int,
    content: String,
    writer: String
) : CommonDateDomain() {
    val commentId: Int = commentId
    var boardId: Int = boardId
    var postId: Int = postId
    var parentComment: Int? = parentComment
    var level: Int = level
    var content: String = content
    var writer: String = writer

    fun update(content: String) {
        this.content = content
        updatedAt = LocalDateTime.now()
    }
}