package com.example.spring.auth.repository

import com.example.spring.auth.model.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Int> {
    fun findByMemId(memId: String): Member?
    fun countByMemId(memId: String): Long
}