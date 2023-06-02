package com.example.spring.member.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class Member() {
    var memIdx:Int = 0
    var memId:String = ""
    @JsonIgnore
    var memPw:String = ""
    var authStatus: Status = Status.NONE

    enum class Status {
        NONE, WRONG_ID, WRONG_PW, AUTHENTIC
    }

    fun comparePW(pw: String): Boolean {
        return BCryptPasswordEncoder().matches(pw, this.memPw)
    }
}