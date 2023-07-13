package com.example.spring.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class PasswordConfig {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()
}