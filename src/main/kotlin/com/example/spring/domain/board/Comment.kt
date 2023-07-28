package com.example.spring.domain.board

import com.example.spring.config.domain.CommonDateDomain
import com.example.spring.config.exception.WriterNotMatchException
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
    var like: Int = 0
    var isLiked: Boolean = true
    var content: String = content
    var childCommentCount: Int = 0
    var writer: String = writer

    fun update(content: String, modifier: String) {
        this.checkWriter(modifier)
        this.content = content
        updatedAt = LocalDateTime.now()
    }

    fun checkWriter(modifier: String): Boolean {
        if (this.writer == modifier) {
            return true
        } else {
            throw WriterNotMatchException()
        }
    }

    fun updateLike(like: Int) {
        this.like = like
        updatedAt = LocalDateTime.now()
    }

    fun updateIsLiked(boolean: Boolean) {
        this.isLiked = boolean
    }
}