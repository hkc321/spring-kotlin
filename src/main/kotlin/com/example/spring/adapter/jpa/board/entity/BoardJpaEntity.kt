package com.example.spring.adapter.jpa.board.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "board")
class BoardJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    var boardId: Int = 0

    @Column(name = "title")
    var title: String = ""

    @Column(name = "content")
    var content: String = ""

    @Column(name = "up")
    var up: Int = 0

    @Column(name = "writer")
    var writer: String = ""

    @Column(name = "created_at")
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    @Column(name = "edited_at")
    var editedAt: String? = null
}