package com.example.spring.adapter.rest.member.dto

import com.example.spring.domain.member.MemberRole

data class MemberCreateRequest (
    val email: String,
    val password: String,
    val role: MemberRole = MemberRole.ROLE_STANDARD
)
