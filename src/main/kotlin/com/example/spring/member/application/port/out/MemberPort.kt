package com.example.spring.member.application.port.out

import com.example.spring.member.domain.Auth
import com.example.spring.member.domain.Member

interface MemberPort {
    /**
     * Member 찾기
     * */
    fun findMember()

    /**
     * Member 등록
     * */
    fun registerMember()

    /**
     * ID,PW 확인
     * */
    fun checkAuth(auth: Auth): Member?
}