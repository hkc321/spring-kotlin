package com.example.spring.adapter.rest.member

import com.example.spring.adapter.rest.member.dto.*
import com.example.spring.adapter.rest.member.mapper.MemberRestMapper
import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.domain.member.Member
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.security.Principal
import java.util.*

@RestController
@RequestMapping("members")
class MemberController(private val memberUseCase: MemberUseCase) {
    private val memberRestMapper = MemberRestMapper.INSTANCE

    @PostMapping("register")
    fun createMember(@RequestBody body: MemberCreateRequest): ResponseEntity<MemberCommonResponse> {
        val createdMember: Member =
            memberUseCase.createMember(
                MemberUseCase.Commend.CreateCommend(
                    email = body.email,
                    password = body.password
                )
            )
        val location = "/members/${createdMember.email}"
        return ResponseEntity.created(URI.create(location)).body(memberRestMapper.toMemberCommonResponse(createdMember))
    }

    @GetMapping("{memberId}")
    fun readMember(
        @PathVariable("memberId") memberId: Int,
        principal: Principal
    ): ResponseEntity<MemberCommonResponse> =
        ResponseEntity.ok(
            memberRestMapper.toMemberCommonResponse(
                memberUseCase.readMember(
                    MemberUseCase.Commend.ReadCommend(
                        memberId,
                        principal.name
                    )
                )
            )
        )

    @PatchMapping("{memberId}")
    fun updateMember(
        @PathVariable("memberId") memberId: Int,
        @RequestBody body: MemberUpdateRequest,
        principal: Principal
    ): ResponseEntity<MemberCommonResponse> =
        ResponseEntity.ok(
            memberRestMapper.toMemberCommonResponse(
                memberUseCase.updateMember(
                    MemberUseCase.Commend.UpdateCommend(
                        memberId,
                        body.password,
                        principal.name
                    )
                )
            )
        )

    @PatchMapping("{memberId}/role")
    fun updateMemberRole(@PathVariable("memberId") memberId: Int, @RequestBody body: MemberUpdateRoleRequest) =
        memberRestMapper.toMemberCommonResponse(memberUseCase.updateMemberRole(MemberUseCase.Commend.UpdateRoleCommend(memberId, body.role)))

    @DeleteMapping("{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMember(@PathVariable("memberId") memberId: Int, principal: Principal) =
        memberUseCase.deleteMember(MemberUseCase.Commend.DeleteCommend(memberId, principal.name))

    /**
     * Spring Security 에서 실행
     * */
    @PostMapping("login")
    fun login(@RequestBody body: MemberLoginRequest): ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("성공")
    }

    /**
     * Spring Security 에서 실행
     * */
    @PostMapping("logout")
    fun logout(): ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("성공")
    }
}