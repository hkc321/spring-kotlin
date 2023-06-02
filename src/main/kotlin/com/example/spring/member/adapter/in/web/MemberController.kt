package com.example.spring.member.adapter.`in`.web

import com.example.spring.member.adapter.AuthRequest
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

//    @PostMapping("register")
//    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Member> {
//        val member = Member()
//        memberService.idCheck(body.memId)
//        member.memId = body.memId
//        member.memPw = body.memPw
//
//        return ResponseEntity.ok(memberService.save(member))
//    }

    @PostMapping("login")
    fun login(@RequestBody body: AuthRequest): ResponseEntity<Any>{
        return memberUseCase.login(body.toDomain())


//        val member = memberService.findByMemId(body.memId)
//            ?: return ResponseEntity.badRequest().body(MessageDTO("user not found"))
//
//        if (!memberService.comparePW(body.memPw, member.memPw)){
//            return ResponseEntity.badRequest().body(MessageDTO("invalid password"))
//        }
//
//        val accessToken = jwtTokenProvider.createAccessToken(member)
//        val refreshToken = jwtTokenProvider.createRefreshToken(member)
//
//        val data = mutableMapOf<String, Any>()
//        data["X-AUTH-TOKEN-ACCESS"] = accessToken
//        data["X-AUTH-TOKEN-REFRESH"] = refreshToken
//
//        return ResponseEntity.ok(data)
    }

    @GetMapping("test")
    fun test(): String {
        return "test"
    }
}