package com.example.spring.adapter.rest.member.dto

data class MemberCommonResponse(
    val memberId: Int,
    val email: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String?
)
