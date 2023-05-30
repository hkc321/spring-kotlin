package com.example.spring.auth.controller

import com.example.spring.auth.dto.LoginDTO
import com.example.spring.auth.dto.MessageDTO
import com.example.spring.auth.dto.RegisterDTO
import com.example.spring.auth.model.Member
import com.example.spring.auth.service.MemberService
import com.example.spring.config.JWTConfig
import com.example.spring.jwt.JwtTokenProvider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api")
class AuthController(private val memberService: MemberService, private val jwtTokenProvider: JwtTokenProvider) {

    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Member> {
        val member = Member()
        memberService.idCheck(body.memId)
        member.memId = body.memId
        member.memPw = body.memPw

        return ResponseEntity.ok(memberService.save(member))
    }

    @PostMapping("login")
    fun login(@RequestBody body: LoginDTO): ResponseEntity<Any>{
        val member = memberService.findByMemId(body.memId)
            ?: return ResponseEntity.badRequest().body(MessageDTO("user not found"))

        if (!memberService.comparePW(body.memPw, member.memPw)){
            return ResponseEntity.badRequest().body(MessageDTO("invalid password"))
        }

        val accessToken = jwtTokenProvider.createAccessToken(member)
        val refreshToken = jwtTokenProvider.createRefreshToken(member)

        val data = mutableMapOf<String, Any>()
        data["X-AUTH-TOKEN-ACCESS"] = accessToken
        data["X-AUTH-TOKEN-REFRESH"] = refreshToken

        return ResponseEntity.ok(data)
    }

    @GetMapping("test")
    fun test(): String {
        return "test"
    }
}