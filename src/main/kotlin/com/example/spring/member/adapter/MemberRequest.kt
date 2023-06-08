package com.example.spring.member.adapter

import com.example.spring.member.domain.Auth
import com.example.spring.member.domain.Member

class MemberRequest() {
    var id: String = ""
    var pw: String = ""

    fun toDomain(): Member {
        Member().let {
            id = this.id
            pw = this.pw

            return it
        }
    }
}

