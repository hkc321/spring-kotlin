package com.example.spring.config.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class CommonDateEntity {
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = true)
    var updatedAt: LocalDateTime? = null
}