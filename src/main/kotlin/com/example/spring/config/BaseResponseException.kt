package com.example.spring.config

import com.example.spring.config.code.ErrorCode

data class BaseResponseException(
    val errorCode: ErrorCode,
    val errorMessage: String
)
