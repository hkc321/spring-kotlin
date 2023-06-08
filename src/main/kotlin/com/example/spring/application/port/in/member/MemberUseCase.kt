package com.example.spring.application.port.`in`.member

import com.example.spring.domain.member.Member
import org.springframework.http.ResponseEntity

interface MemberUseCase {
    /**
     * 회원가입
     * */
    fun join(member: Member): ResponseEntity<Any>
    fun logout()
}