package com.example.spring.config.security

import com.example.spring.application.service.member.JwtService
import com.example.spring.config.filter.CustomJwtAuthorizationFilter
import com.example.spring.config.filter.CustomUsernamePasswordAuthenticationFilter
import com.example.spring.config.filter.JwtAuthorizationExceptionFilter
import com.example.spring.config.filter.corsFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.filter.CorsFilter


@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val jwtService: JwtService,
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customLogoutSuccessHandler: CustomLogoutSuccessHandler
) {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer {
            it.ignoring()
                .requestMatchers("/swagger-ui.html")
                .requestMatchers("/static/**")
            it.ignoring()
                .requestMatchers("/v1/members/register")
                .requestMatchers("/v1/members/token")
        }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            httpBasic { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeRequests {
                authorize("/v1/members/login", permitAll)
                authorize("/v1/members/register", permitAll)
                authorize("/v1/members/{memberId}/role", hasRole("ROLE_ADMIN"))
                authorize(anyRequest, permitAll)
            }
            usernamePasswordAuthenticationFilter()?.let { addFilterAt<UsernamePasswordAuthenticationFilter>(it) }
            addFilterAt<CorsFilter>(corsFilter())
            addFilterBefore<BasicAuthenticationFilter>(
                CustomJwtAuthorizationFilter(
                    jwtService
                )
            )
            addFilterBefore<CustomJwtAuthorizationFilter>(JwtAuthorizationExceptionFilter(jwtService))
            exceptionHandling {
                accessDeniedHandler = customAccessDeniedHandler
                authenticationEntryPoint = customAuthenticationEntryPoint
            }
            formLogin { disable() }
            logout {
                logoutUrl = "/v1/members/logout"
                logoutSuccessHandler = customLogoutSuccessHandler
            }
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
            jwtService
        ).apply { setFilterProcessesUrl("/v1/members/login") }
    }

}