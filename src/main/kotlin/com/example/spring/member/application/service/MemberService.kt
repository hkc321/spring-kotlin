package com.example.spring.member.application.service

import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.application.port.`in`.MemberUseCase
import com.example.spring.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberPort: MemberPort,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager): MemberUseCase {
    /**
     * 회원가입
     * */
    override fun join(member: Member): ResponseEntity<Any> {
        val joinMember: Member? = memberPort.registerMember(member)

        if (joinMember == null){
            return ResponseEntity.badRequest().body("ID already exists")
        }else{
            return ResponseEntity.ok(joinMember)
        }
    }

    override fun login(member: Member): ResponseEntity<Any> {
//        try {
//            val authentication: Authentication =
//            authenticationManager.authenticate(
//                UsernamePasswordAuthenticationToken(member.id, member.pw, null)
//            )
//            val accessToken = jwtService.createAccessToken(authentication)
//            val refreshToken = jwtService.createRefreshToken(authentication)
//            val data = mutableMapOf<String, Any>()
//            data["X-AUTH-TOKEN-ACCESS"] = accessToken
//            data["X-AUTH-TOKEN-REFRESH"] = refreshToken
//            return ResponseEntity.ok(authentication)
//        }catch (e: Exception){
//            return ResponseEntity.badRequest().body("Bad Credential")
//        }


        val findMember: Member? =  memberPort.checkAuth(member)
        if (findMember != null) {
            if (findMember.authStatus == Member.Status.WRONG_PW){
                return ResponseEntity.badRequest().body("invalid password")
            }else if (findMember.authStatus == Member.Status.AUTHENTIC){
//                val accessToken = jwtService.createAccessToken(findMember)
//                val refreshToken = jwtService.createRefreshToken(findMember)
//                val data = mutableMapOf<String, Any>()
//                data["X-AUTH-TOKEN-ACCESS"] = accessToken
//                data["X-AUTH-TOKEN-REFRESH"] = refreshToken
                return ResponseEntity.ok("qwe")
            }
        }

        return ResponseEntity.badRequest().body("user not found")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}