package com.example.spring.application.service.member

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.domain.member.Member
import com.example.spring.domain.member.MemberRole
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberJpaPort: MemberJpaPort,
    private val passwordEncoder: BCryptPasswordEncoder
) : MemberUseCase {
    @Transactional
    override fun createMember(commend: MemberUseCase.Commend.CreateCommend): Member =
        memberJpaPort.createMember(
            Member(
                email = commend.email,
                password = passwordEncoder.encode(commend.password),
                role = MemberRole.ROLE_STANDARD.name
            )
        )

    @Transactional(readOnly = true)
    override fun readMember(commend: MemberUseCase.Commend.ReadCommend): Member {
        val member: Member = memberJpaPort.findMemberByMemberId(commend.memberId)
        member.checkAccessor(commend.accessor)

        return member
    }


    @Transactional
    override fun updateMember(commend: MemberUseCase.Commend.UpdateCommend): Member {
        val member: Member = memberJpaPort.findMemberByMemberId(commend.memberId)
        member.update(passwordEncoder.encode(commend.password), commend.accessor)
        return memberJpaPort.updateMember(member)
    }
    @Transactional
    override fun updateMemberRole(commend: MemberUseCase.Commend.UpdateRoleCommend): Member {
        val member: Member = memberJpaPort.findMemberByMemberId(commend.memberId)
        member.updateRole(commend.role)
        return memberJpaPort.updateMemberRole(member)
    }

    @Transactional
    override fun deleteMember(commend: MemberUseCase.Commend.DeleteCommend) {
        val member: Member = memberJpaPort.findMemberByMemberId(commend.memberId)
        member.checkAccessor(commend.accessor)

        memberJpaPort.deleteMember(member.memberId)
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    @Transactional(readOnly = true)
    override fun findMemberByRefreshToken(commend: MemberUseCase.Commend.FindMemberByRefreshTokenCommend): Member =
        memberJpaPort.findMemberByRefreshToken(commend.token)
}