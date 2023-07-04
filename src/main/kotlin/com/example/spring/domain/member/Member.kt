package com.example.spring.domain.member

import com.example.spring.config.domain.CommonDateDomain
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.password.PasswordEncoder


class Member(
    memberId: Int = 0,
    email: String,
    password: String,
    role: String,
) : CommonDateDomain() {
    val memberId: Int = memberId
    var email: String = email

    @JsonIgnore
    var password: String = password
    var role: String = role
    var refreshToken: String? = null

    fun update(password: String) {
        this.password = password
    }

    fun saveRefreshToken(token: String) {
        this.refreshToken = token
    }

    fun comparePassword(encoder: PasswordEncoder, password: String): Boolean {
        return encoder.matches(password, this.password)
    }
}