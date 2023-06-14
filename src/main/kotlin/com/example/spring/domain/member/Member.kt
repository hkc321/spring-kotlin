package com.example.spring.domain.member

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


class Member {
    var memberId: Int = 0
    var email: String = ""

    @JsonIgnore
    var pw: String = ""
    var authStatus: Status = Status.NONE
    var role: String = ""
    var refreshToken: String? = null

    enum class Status {
        NONE, WRONG_ID, WRONG_PW, AUTHENTIC
    }

    fun comparePW(pw: String): Boolean {
        return BCryptPasswordEncoder().matches(pw, this.pw)
    }
}