package com.example.spring.adapter.rest.member.dto

import com.example.spring.domain.member.MemberRole

data class MemberUpdateRoleRequest(
    val role: MemberRole
)
