package com.example.spring.member.adapter.out.persistence

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name="member")
class MemberEntity{
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
            field = BCryptPasswordEncoder().encode(value)
        }



}