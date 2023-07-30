package com.example.spring.config.route

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerTypePredicate
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ApiPrefixConfiguration : WebMvcConfigurer {
    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.addPathPrefix("/v1", HandlerTypePredicate.forBasePackage("com.example.spring"))
    }
}