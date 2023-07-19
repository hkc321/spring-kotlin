package com.example.spring.config.dto

import com.example.spring.adapter.rest.member.dto.MemberCommonResponse
import com.example.spring.domain.member.Member

data class LoginSuccessResponse(
    val success: Boolean,
    val member: MemberCommonResponse
)
