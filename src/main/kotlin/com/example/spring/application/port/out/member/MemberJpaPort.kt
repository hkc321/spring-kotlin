package com.example.spring.application.port.out.member

import com.example.spring.domain.member.Member

interface MemberJpaPort {
    /**
     * Member 찾기
     * */
    fun findMemberByEmail(email: String): Member?

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
     * Member Role 수정
     * */
    fun updateMemberRole(member: Member): Member

    /**
     * Member 삭제
     * */
    fun deleteMember(memberId: Int)

}