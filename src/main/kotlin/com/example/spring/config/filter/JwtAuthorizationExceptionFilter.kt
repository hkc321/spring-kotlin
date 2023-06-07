package com.example.spring.config.filter

import com.example.spring.member.application.service.JwtService
import com.example.spring.member.domain.Jwt
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthorizationExceptionFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response) // -> CustomJwtAuthorizationFilter 진행
        } catch (ex: ExpiredJwtException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.EXPIRED_EXCEPTION, ex)
        } catch (ex: JwtException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, Jwt.JWT_EXCEPTION, ex)
        } catch (ex: Exception) {
            setErrorResponse(response, HttpStatus.BAD_REQUEST, Jwt.EXCEPTION, ex)
        }
    }

    fun setErrorResponse(res: HttpServletResponse, status: HttpStatus, errorType: String, ex: Throwable) {
        jwtService.setErrorResponseMessage(res, status, errorType, ex.message ?: "")
    }
}

data class JwtExceptionResponse(
    val status: HttpStatus,
    val message: String,
) {
    fun toJsonString(): String {
        return this.toString()
    }
}