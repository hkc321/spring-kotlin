package com.example.spring.auth.service

import com.example.spring.auth.model.Member
import com.example.spring.auth.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(private val memberRepository: MemberRepository) {

    fun save(member: Member): Member {
        return this.memberRepository.save(member)
    }

    fun findByMemId(memId: String): Member? {
        return  this.memberRepository.findByMemId(memId)
    }
}