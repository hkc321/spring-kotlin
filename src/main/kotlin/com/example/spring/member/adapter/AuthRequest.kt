package com.example.spring.member.adapter

import com.example.spring.member.domain.Auth

class AuthRequest() {
    var memId:String = ""
    var memPw:String = ""

    fun toDomain(): Auth {
        val auth = Auth()
        auth.memId = this.memId
        auth.memPw = this.memPw

        return auth
    }
}

