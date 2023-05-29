package com.example.spring.auth.controller

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
        member.mem_id = body.mem_id
        member.mem_pw = body.mem_pw
        print(member)

        return ResponseEntity.ok(this.memberService.save(member))
    }

    @GetMapping("test")
    fun test(): String {
        return "test"
    }
}