package com.example.spring.domain.board

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Comment {
    var commentId: Int = 0
    var boardId: Int = 0
    var parentCommentId: Int? = null
    var level: Int = 1
    var content: String = ""
    var up: Int = 0
    var writer: String = ""
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    var updatedAt: String? = null
    var childCommentCount: Int = 0

    fun updateComment(comment: Comment): Comment {
        this.content = comment.content
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return this
    }
}