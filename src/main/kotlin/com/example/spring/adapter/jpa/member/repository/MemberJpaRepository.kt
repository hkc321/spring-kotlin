package com.example.spring.adapter.jpa.member.repository

import com.example.spring.adapter.jpa.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<MemberEntity, Int> {
    fun findById(id: String): MemberEntity?
    fun countById(id: String): Long
    fun findByMemberId(memberId: Int): MemberEntity?
    fun findByRefreshToken(token: String): MemberEntity
}