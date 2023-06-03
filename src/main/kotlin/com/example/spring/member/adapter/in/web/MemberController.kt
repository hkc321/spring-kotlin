package com.example.spring.member.adapter.`in`.web

import com.example.spring.member.adapter.MemberRequest
import com.example.spring.member.application.port.`in`.MemberUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api")
class MemberController(private val memberUseCase: MemberUseCase) {

    @PostMapping("register")
    fun register(@RequestBody body: MemberRequest): ResponseEntity<Any> {
        return memberUseCase.join(body.toDomain())
    }

    @PostMapping("login")
    fun login(@RequestBody body: MemberRequest): ResponseEntity<Any>{
        return memberUseCase.login(body.toDomain())
    }

    @GetMapping("test")
    fun test(): String {
        return "test"
    }
}