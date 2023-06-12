package com.example.spring.config.dto

import org.springframework.http.HttpStatus

open class BaseResponseDto {
    var success: Boolean = true
    var status: Int = HttpStatus.OK.value()
}