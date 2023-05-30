package com.example.spring.auth.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
private val passwordEncoder = BCryptPasswordEncoder()

@Entity
@Table(name="member")
class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_idx", nullable = false)
    var memIdx: Int = 0

    @Column(name = "mem_id", unique = true)
    var memId: String = ""

    @Column(name = "mem_pw")
    var memPw: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            field = passwordEncoder.encode(value)
        }

//    fun comparePW(pw: String): Boolean {
//        return BCryptPasswordEncoder().matches(pw, this.memPw)
//    }
}
