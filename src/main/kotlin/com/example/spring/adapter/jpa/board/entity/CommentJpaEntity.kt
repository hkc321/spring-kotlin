package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.config.entity.CommonDateEntity
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicUpdate

@DynamicUpdate
@Entity
@Table(name = "comment")
class CommentJpaEntity(
    commentId: Int = 0,
    board: BoardJpaEntity,
    post: PostJpaEntity,
    parentComment: CommentJpaEntity? = null,
    level: Int,
    content: String,
    writer: MemberJpaEntity
) : CommonDateEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    val commentId: Int = commentId

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    var board: BoardJpaEntity = board

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    var post: PostJpaEntity = post

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_comment_id", nullable = true)
    var parentComment: CommentJpaEntity? = parentComment

    @Column(nullable = false)
    var level: Int = level

    @Column(nullable = false)
    @ColumnDefault("0")
    var up: Int = 0

    @Column(nullable = false)
    var content: String = content

    @ManyToOne
    @JoinColumn(name = "writer", nullable = false)
    var writer: MemberJpaEntity = writer
}