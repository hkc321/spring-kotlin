package com.example.spring.adapter.rest.member.dto

import com.example.spring.domain.member.Member

class MemberRequest {
    var email: String = ""
    var pw: String = ""

    fun toDomain(): Member {
        val member = Member()
        member.email = this.email
        member.pw = this.pw

        return member
    }
}

