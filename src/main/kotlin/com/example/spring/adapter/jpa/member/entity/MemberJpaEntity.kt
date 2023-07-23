package com.example.spring.adapter.jpa.member.entity

import com.example.spring.config.entity.CommonDateEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NaturalId
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

private val passwordEncoder = BCryptPasswordEncoder()
@DynamicUpdate
@Entity
@Table(name = "member")
class MemberJpaEntity(
    memberId: Int = 0,
    email: String,
    password: String,
    role: String
) : CommonDateEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    var memberId: Int = memberId

    @NaturalId
    @Column(name = "email", unique = true)
    var email: String = email

    @Column(name = "password")
    @JsonIgnore
    var password: String = password

    @Column(name = "role")
    var role: String = role
}