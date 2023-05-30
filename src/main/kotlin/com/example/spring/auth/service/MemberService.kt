package com.example.spring.auth.service

import com.example.spring.auth.dto.MessageDTO
import com.example.spring.auth.model.Member
import com.example.spring.auth.repository.MemberRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class MemberService(private val memberRepository: MemberRepository) {

    fun save(member: Member): Member {
        return memberRepository.save(member)
    }

    fun findByMemId(memId: String): Member? {
        return  memberRepository.findByMemId(memId)
    }

    fun comparePW(requestPassword: String, existingPassword: String): Boolean {
        return BCryptPasswordEncoder().matches(requestPassword, existingPassword)
    }

    fun idCheck(id:String): Any {
        if (memberRepository.countByMemId(id) > 0) {
            return ResponseEntity.badRequest().body(MessageDTO("existing id"))
        }else{
            return true
        }
    }
}