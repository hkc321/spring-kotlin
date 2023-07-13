package com.example.spring.config

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.service.member.JwtService
import com.example.spring.application.service.member.UserDetailsServiceImpl
import com.example.spring.config.filter.CustomJwtAuthorizationFilter
import com.example.spring.config.filter.CustomUsernamePasswordAuthenticationFilter
import com.example.spring.config.filter.JwtAuthorizationExceptionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val jwtService: JwtService,
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val memberUseCase: MemberUseCase,
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer {
            it.ignoring()
                .requestMatchers("/swagger-ui.html")
                .requestMatchers("/static/**")
            it.ignoring()
                .requestMatchers("/members/register")
        }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            httpBasic { disable() }
            authorizeRequests {
                authorize("/members/login", permitAll)
                authorize("/members/register", permitAll)
                authorize("/members/{memberId}/role", hasRole("ROLE_ADMIN"))
                authorize(anyRequest, permitAll)
            }
            usernamePasswordAuthenticationFilter()?.let { addFilterAt<UsernamePasswordAuthenticationFilter>(it) }
            addFilterBefore<BasicAuthenticationFilter>(
                CustomJwtAuthorizationFilter(
                    jwtService,
                    memberUseCase,
                    userDetailsServiceImpl
                )
            )
            addFilterBefore<CustomJwtAuthorizationFilter>(JwtAuthorizationExceptionFilter(jwtService))
            exceptionHandling {
                accessDeniedHandler = customAccessDeniedHandler
                authenticationEntryPoint = customAuthenticationEntryPoint
            }
            formLogin { disable() }
            logout { }
        }
        return http.build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    fun usernamePasswordAuthenticationFilter(): UsernamePasswordAuthenticationFilter? {
        return CustomUsernamePasswordAuthenticationFilter(
            authenticationManager(authenticationConfiguration),
            jwtService,
            memberUseCase
        ).apply { setFilterProcessesUrl("/members/login") }
    }

}