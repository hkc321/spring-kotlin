package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.config.entity.CommonDateEntity
import com.example.spring.domain.member.MemberRole
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@DynamicUpdate
@Entity
@Table(name = "qwe")
class qwe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qwe_id", nullable = false)
    val qweId: Int = 0

    @Enumerated(EnumType.STRING)
    val qqq: MemberRole = MemberRole.ROLE_STANDARD
}