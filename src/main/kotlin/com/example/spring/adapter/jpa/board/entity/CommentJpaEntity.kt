package com.example.spring.adapter.jpa.board.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "comment")
class CommentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    var commentId: Int = 0

    @Column(name = "board_id")
    var boardId: Int = 0

    @Column(name = "parent_comment_id")
    var parentCommentId: Int? = null

    @Column(name = "level")
    var level: Int = 1

    @Column(name = "content")
    var content: String = ""

    @Column(name = "up")
    var up: Int = 0

    @Column(name = "writer")
    var writer: String = ""

    @Column(name = "created_at")
    var createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    @Column(name = "updated_at")
    var updatedAt: String? = null

}