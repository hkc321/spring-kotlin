package com.example.spring.adapter.jpa.member.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "member")
class MemberJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    var memberId: Int = 0

    @Column(name = "email", unique = true)
    var email: String = ""

    @Column(name = "pw")
    var pw: String = ""
        @JsonIgnore
        get
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }

    @Column(name = "role")
    var role: String = ""

    @Column(name = "refresh_token")
    var refreshToken: String? = null


}