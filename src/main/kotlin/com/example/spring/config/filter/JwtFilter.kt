package com.example.spring.config.filter

import com.example.spring.member.adapter.out.persistence.MemberAdapter
import com.example.spring.member.application.service.JwtService
import com.example.spring.member.domain.Member
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(private val jwtService: JwtService): OncePerRequestFilter() {
    /**
     * Same contract as for `doFilter`, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See [.shouldNotFilterAsyncDispatch] for details.
     *
     * Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 헤더에 Authorization이 있다면 가져온다.
        val authorizationHeader: String? = jwtService.resolveToken(request) ?: return filterChain.doFilter(request, response)
        // Bearer타입 토큰이 있을 때 가져온다.
        val token = authorizationHeader?.substring("Bearer ".length) ?: return filterChain.doFilter(request, response)

        // 토큰 검증
        if (token != null && jwtService.validateToken(token)) {
            // 토큰에서 username 파싱
            val id = jwtService.extractId(token)
            // username으로 AuthenticationToken 생성
            val authentication: UsernamePasswordAuthenticationToken = jwtService.getAuthentication(id)
            // 생성된 AuthenticationToken을 SecurityContext가 관리하도록 설정
            SecurityContextHolder.getContext().authentication = authentication
            println(authentication)
        }

        filterChain.doFilter(request, response)
    }
}