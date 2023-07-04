package com.example.spring.adapter.jpa.member.repository

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, Int> {
    fun findByEmail(email: String): MemberJpaEntity?
    fun findByMemberId(memberId: Int): MemberJpaEntity?
    fun findByRefreshToken(token: String): MemberJpaEntity?
}