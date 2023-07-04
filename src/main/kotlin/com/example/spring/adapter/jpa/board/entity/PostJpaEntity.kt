package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.config.entity.CommonDateEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@DynamicUpdate
@Entity
@Table(name = "post")
class PostJpaEntity(
    postId: Int = 0,
    board: BoardJpaEntity,
    title: String,
    content: String,
    writer: MemberJpaEntity
) : CommonDateEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    val postId: Int = postId

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    var board: BoardJpaEntity = board

    @Column(nullable = false)
    var title: String = title

    @Column(nullable = false)
    var content: String = content

    @Column(nullable = false)
    var up: Int = 0

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer", nullable = false)
    var writer: MemberJpaEntity = writer
}