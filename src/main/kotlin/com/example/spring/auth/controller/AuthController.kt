package com.example.spring.auth.controller

import com.example.spring.auth.dto.LoginDTO
import com.example.spring.auth.dto.MessageDTO
import com.example.spring.auth.dto.RegisterDTO
import com.example.spring.auth.model.Member
import com.example.spring.auth.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class AuthController(private val memberService: MemberService) {

    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Member> {
        val member = Member()
        member.memId = body.memId
        member.memPw = body.memPw

        return ResponseEntity.ok(this.memberService.save(member))
    }

    @PostMapping("login")
    fun login(@RequestBody body: LoginDTO): ResponseEntity<Any>{
        val member = this.memberService.findByMemId(body.memId)
            ?: return ResponseEntity.badRequest().body(MessageDTO("user not found"))

        if (!member.comparePW(body.memPw)){
            return ResponseEntity.badRequest().body(MessageDTO("invalid password"))
        }

        return ResponseEntity.ok(member)
    }

    @GetMapping("test")
    fun test(): String {
        return "test"
    }
}