package com.example.spring.config.dto

data class JwtExceptionResponse(
    val errorCode: String,
    val errorMessage: String,
) {
    fun toJsonString(): String {
        return this.toString()
    }
}
