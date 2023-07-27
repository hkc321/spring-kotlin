package com.example.spring.adapter.rest.member.dto

import jakarta.validation.constraints.Email

data class MemberCreateRequest (
    @field: Email(message = "올바른 이메일 주소를 입력해주세요")
    val email: String,
    val password: String
)
