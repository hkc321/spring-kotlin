package com.example.spring.member.application.port.`in`

import com.example.spring.member.domain.Member
import org.springframework.http.ResponseEntity

interface MemberUseCase {
    /**
     * 회원가입
     * */
    fun join(member: Member): ResponseEntity<Any>
    fun login(member: Member): ResponseEntity<Any>
    fun logout()
}