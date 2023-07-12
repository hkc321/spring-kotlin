package com.example.spring.domain.member

import com.example.spring.config.AccessorNotMatchException
import com.example.spring.config.domain.CommonDateDomain
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime


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

    fun update(password: String, accessor: String) {
        this.checkAccessor(accessor)
        this.password = password
        this.updatedAt = LocalDateTime.now()
    }

    fun updateRole(role: MemberRole) {
        this.role = role.name
        this.updatedAt = LocalDateTime.now()
    }

    fun saveRefreshToken(token: String) {
        this.refreshToken = token
        this.updatedAt = LocalDateTime.now()
    }

    fun checkAccessor(accessor: String): Boolean {
        if (this.email == accessor ) {
            return true
        } else {
            throw AccessorNotMatchException()
        }
    }

    fun comparePassword(encoder: PasswordEncoder, password: String): Boolean {
        return encoder.matches(password, this.password)
    }
}