package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.config.entity.CommonDateEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@DynamicUpdate
@Entity
@Table(name = "board")
class BoardJpaEntity(
    boardId: Int = 0,
    name: String,
    description: String,
    writer: MemberJpaEntity,
    modifier: MemberJpaEntity
) : CommonDateEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    val boardId: Int = boardId

    @Column(nullable = false, unique = true)
    var name: String = name

    @Column(nullable = false)
    var description: String = description

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer", nullable = false)
    var writer: MemberJpaEntity = writer

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "modifier", nullable = true)
    var modifier: MemberJpaEntity = modifier
}