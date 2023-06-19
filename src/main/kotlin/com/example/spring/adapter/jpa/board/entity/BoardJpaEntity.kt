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

    @Column(name = "title", nullable = false)
    var title: String = ""

    @Column(name = "content", nullable = false)
    var content: String = ""

    @Column(name = "up")
    var up: Int = 0

    @Column(name = "writer", nullable = false)
    var writer: String = ""

    @Column(name = "created_at")
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    @Column(name = "updated_at")
    var updatedAt: String? = null
}