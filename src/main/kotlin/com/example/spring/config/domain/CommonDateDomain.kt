package com.example.spring.config.domain

import java.time.LocalDateTime

abstract class CommonDateDomain {
    var createdAt: LocalDateTime = LocalDateTime.now()
    var updatedAt: LocalDateTime? = null
}