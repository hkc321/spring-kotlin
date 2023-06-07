package com.example.spring.member.application.port.out

import com.example.spring.member.adapter.out.persistence.MemberEntity
import com.example.spring.member.domain.Member
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

interface MemberPort {
    /**
     * Member 찾기
     * */
    fun findMemberById(id: String): MemberEntity?

    /**
     * Member 찾기
     * */
    fun findMemberByMemberId(memberId: Int): Member?

    /**
     * Member 등록
     * */
    fun registerMember(member: Member): Member?

    /**
     * ID,PW 확인
     * */
    fun checkAuth(member: Member): Member?

    /**
     * Refresh 토큰 저장
     * */
    fun saveRefreshToken(id: String, token: String)

    /**
     * Member 찾기
     * */
    fun findMemberByRefreshToken(token: String): MemberEntity
}