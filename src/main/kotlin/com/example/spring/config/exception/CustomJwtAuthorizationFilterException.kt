package com.example.spring.config.exception

data class CustomJwtAuthorizationFilterException(
    val code: String,
    override val message: String,
): RuntimeException(message, null)
