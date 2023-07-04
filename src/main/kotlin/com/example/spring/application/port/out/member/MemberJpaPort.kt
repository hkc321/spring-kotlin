package com.example.spring.application.port.out.member

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.domain.member.Member

interface MemberJpaPort {
    /**
     * Member 찾기
     * */
    fun findMemberByEmail(email: String): Member

    /**
     * Member 찾기
     * */
    fun findMemberByMemberId(memberId: Int): Member?

    /**
     * Member 등록
     * */
    fun createMember(member: Member): Member

    /**
     * Member 수정
     * */
    fun updateMember(member: Member): Member

    /**
     * Member 삭제
     * */
    fun deleteMember(email: String)

    /**
     * Refresh 토큰 저장
     * */
    fun saveRefreshToken(member: Member): Member

    /**
     * Member 찾기
     * */
    fun findMemberByRefreshToken(token: String): Member
}