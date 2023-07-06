package com.example.spring.domain.board

import com.example.spring.config.domain.CommonDateDomain
import java.time.LocalDateTime

class Post(
    postId: Int = 0,
    boardId: Int,
    title: String,
    content: String,
    writer: String
) : CommonDateDomain() {
    val postId: Int = postId
    var boardId: Int = boardId
    var title: String = title
    var content: String = content
    var up: Int = 0
    var writer: String = writer

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
        updatedAt = LocalDateTime.now()
    }

    fun plusUp() = up++
}