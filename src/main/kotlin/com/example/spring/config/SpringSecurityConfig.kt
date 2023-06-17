package com.example.spring.config

import com.example.spring.config.filter.CustomJwtAuthorizationFilter
import com.example.spring.config.filter.CustomUsernamePasswordAuthenticationFilter
import com.example.spring.config.filter.JwtAuthorizationExceptionFilter
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val jwtService: JwtService,
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val memberJpaPort: MemberJpaPort,
    private val userDetailsServiceImpl: UserDetailsServiceImpl
) {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer {
            it.ignoring()
                .requestMatchers("/swagger-ui.html")
                .requestMatchers("/static/**")
            it.ignoring()
                .requestMatchers("/api/register")
                .requestMatchers("/comment/**")
                .requestMatchers("/board/**")
        }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf {
                disable()
            }

            authorizeRequests {
                authorize("/api/login", permitAll)
                authorize("/api/register", permitAll)
                authorize("/api/why", hasAuthority("b"))
                authorize(anyRequest, authenticated)
            }
            usernamePasswordAuthenticationFilter()?.let { addFilterAt<UsernamePasswordAuthenticationFilter>(it) }
            addFilterBefore<BasicAuthenticationFilter>(
                CustomJwtAuthorizationFilter(
                    jwtService,
                    memberJpaPort,
                    userDetailsServiceImpl
                )
            )
            addFilterBefore<CustomJwtAuthorizationFilter>(JwtAuthorizationExceptionFilter(jwtService))
            formLogin { disable() }
            logout { }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    fun usernamePasswordAuthenticationFilter(): UsernamePasswordAuthenticationFilter? {
        return CustomUsernamePasswordAuthenticationFilter(
            authenticationManager(authenticationConfiguration),
            jwtService,
            memberJpaPort
        ).apply { setFilterProcessesUrl("/api/login") }
    }

}