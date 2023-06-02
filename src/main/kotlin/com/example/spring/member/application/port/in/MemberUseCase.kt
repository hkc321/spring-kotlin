package com.example.spring.member.application.port.`in`

import com.example.spring.member.domain.Auth
import org.springframework.http.ResponseEntity

interface MemberUseCase {
    /**
     * 회원가입
     * */
    fun join()
    fun login(auth: Auth): ResponseEntity<Any>
    fun logout()
}