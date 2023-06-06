package com.example.spring.member.application.port.out

import com.example.spring.member.domain.Member
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

interface MemberPort {
    /**
     * Member 찾기
     * */
    fun findMemberById(id: String): Member?

    /**
     * Member 찾기
     * */
    fun findMemberByIdx(idx: Int): Member?

    /**
     * Member 등록
     * */
    fun registerMember(member: Member): Member?

    /**
     * ID,PW 확인
     * */
    fun checkAuth(member: Member): Member?
}