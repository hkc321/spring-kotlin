package com.example.spring.member.adapter.out.persistence

import com.example.spring.member.adapter.out.persistence.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Int> {
    fun findByMemId(memId: String): MemberEntity?
    fun countByMemId(memId: String): Long
}