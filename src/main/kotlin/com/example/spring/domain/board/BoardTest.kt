package com.example.spring.domain.board

import com.example.spring.config.domain.CommonDateDomain
import java.time.LocalDateTime

class BoardTest(
    boardId: Int = 0,
    name: String,
    description: String,
    writer: String,
    modifier: String
) : CommonDateDomain() {
    val boardId: Int = boardId
    var name: String = name
    var description: String = description
    var writer: String = writer
    var modifier: String = modifier

    fun update(name: String, description: String, modifier: String) {
        this.name = name
        this.description = description
        this.modifier = modifier
        this.updatedAt = LocalDateTime.now()
    }
}