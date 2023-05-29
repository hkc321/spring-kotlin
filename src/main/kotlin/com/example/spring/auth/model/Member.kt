package com.example.spring.auth.model

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
    var mem_idx: Int? = null

    @Column(name = "mem_id")
    var mem_id: String? = null

    @Column(name = "mem_pw")
    var mem_pw: String? = null
        set(value) {
            field = passwordEncoder.encode(value)
        }
}
