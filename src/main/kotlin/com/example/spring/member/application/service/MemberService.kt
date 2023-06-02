package com.example.spring.member.application.service

import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.application.port.`in`.MemberUseCase
import com.example.spring.member.domain.Auth
import com.example.spring.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class MemberService(private val memberPort: MemberPort, private val jwtService: JwtService): MemberUseCase {
    /**
     * 회원가입
     * */
    override fun join() {
        TODO("Not yet implemented")
    }

    override fun login(auth: Auth): ResponseEntity<Any> {
        val member: Member? =  memberPort.checkAuth(auth)

        if (member != null) {
            if (member.authStatus == Member.Status.WRONG_PW){
                return ResponseEntity.badRequest().body("invalid password")
            }else if (member.authStatus == Member.Status.AUTHENTIC){
                val accessToken = jwtService.createAccessToken(member)
                val refreshToken = jwtService.createRefreshToken(member)
                val data = mutableMapOf<String, Any>()
                data["X-AUTH-TOKEN-ACCESS"] = accessToken
                data["X-AUTH-TOKEN-REFRESH"] = refreshToken
                return ResponseEntity.ok(data)
            }
        }

        return ResponseEntity.badRequest().body("user not found")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}