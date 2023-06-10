package com.example.spring.adapter.rest.member.dto

import com.example.spring.domain.member.Member

class MemberRequest {
    var id: String = ""
    var pw: String = ""

    fun toDomain(): Member {
        val member = Member()
        member.id = this.id
        member.pw = this.pw

        return member
    }
}

