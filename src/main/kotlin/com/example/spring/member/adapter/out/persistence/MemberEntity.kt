package com.example.spring.member.adapter.out.persistence

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "member")
class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    var memberId: Int = 0

    @Column(name = "id", unique = true)
    var id: String = ""

    @Column(name = "pw")
    var pw: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }

    @Column(name = "role")
    var role: String = ""

    @Column(name = "refresh_token")
    var refreshToken: String? = null


}