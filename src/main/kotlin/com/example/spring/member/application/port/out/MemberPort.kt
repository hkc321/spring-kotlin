package com.example.spring.member.application.port.out

import com.example.spring.member.domain.Member

interface MemberPort {
    /**
     * Member 찾기
     * */
    fun findMemberById()

    /**
     * Member 등록
     * */
    fun registerMember(member: Member): Member?

    /**
     * ID,PW 확인
     * */
    fun checkAuth(member: Member): Member?
}