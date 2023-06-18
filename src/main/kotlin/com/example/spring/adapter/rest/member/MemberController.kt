package com.example.spring.adapter.rest.member

import com.example.spring.adapter.rest.member.dto.MemberRequest
import com.example.spring.application.port.`in`.member.MemberUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("member")
class MemberController(private val memberUseCase: MemberUseCase) {

    @PostMapping("register")
    fun register(@RequestBody body: MemberRequest): ResponseEntity<Any> {
        return memberUseCase.join(body.toDomain())
    }

    @PostMapping("login")
    fun login(@RequestBody body: MemberRequest): ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("성공")
    }

    @PostMapping("why")
    fun why(): ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("성공")
    }

    @GetMapping("test")
    fun test(): String {
        return "test"
    }
}