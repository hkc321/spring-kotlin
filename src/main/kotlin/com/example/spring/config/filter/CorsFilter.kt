package com.example.spring.config.filter

import org.springframework.context.annotation.Bean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Bean
fun corsFilter(): CorsFilter {
    val source = UrlBasedCorsConfigurationSource()
    val config = CorsConfiguration()
    config.allowCredentials = true
    config.addAllowedOrigin("http://localhost:8080")
//    config.addAllowedOriginPattern("")
    config.addAllowedHeader("*")
    config.addExposedHeader("Authorization")
    config.addAllowedMethod("*")
    source.registerCorsConfiguration("/**", config)
    return CorsFilter(source)
}