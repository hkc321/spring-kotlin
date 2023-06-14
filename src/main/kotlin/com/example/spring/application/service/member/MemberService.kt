package com.example.spring.application.service.member

import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.domain.member.Member
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberJpaPort: MemberJpaPort
) : MemberUseCase {
    /**
     * 회원가입
     * */
    override fun join(member: Member): ResponseEntity<Any> {
        val joinMember: Member? = memberJpaPort.registerMember(member)

        if (joinMember == null) {
            return ResponseEntity.badRequest().body("email already exists")
        } else {
            return ResponseEntity.ok(joinMember)
        }
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}