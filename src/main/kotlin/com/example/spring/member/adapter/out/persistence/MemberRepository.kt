package com.example.spring.member.adapter.out.persistence

import com.example.spring.member.adapter.out.persistence.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Int> {
    fun findById(id: String): MemberEntity?
    fun countById(id: String): Long
    fun findByIdx(idx: Int): MemberEntity?
}