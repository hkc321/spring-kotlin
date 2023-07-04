package com.example.spring.application.service.member

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.domain.member.Member
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
                role = commend.role.name
            )
        )

    @Transactional(readOnly = true)
    override fun readMember(commend: MemberUseCase.Commend.ReadCommend): Member =
        memberJpaPort.findMemberByEmail(commend.email)

    @Transactional
    override fun updateMember(commend: MemberUseCase.Commend.UpdateCommend): Member {
        val member: Member = memberJpaPort.findMemberByEmail(commend.email)
        member.update(passwordEncoder.encode(commend.password))
        return memberJpaPort.updateMember(member)
    }

    @Transactional
    override fun deleteMember(commend: MemberUseCase.Commend.DeleteCommend) =
        memberJpaPort.deleteMember(commend.email)

    override fun logout() {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun saveRefreshToken(commend: MemberUseCase.Commend.SaveRefreshTokenCommend): Member {
        val member: Member = memberJpaPort.findMemberByEmail(commend.email)
        member.saveRefreshToken(commend.token)

        return memberJpaPort.saveRefreshToken(member)
    }

    @Transactional(readOnly = true)
    override fun findMemberByRefreshToken(commend: MemberUseCase.Commend.FindMemberByRefreshTokenCommend): Member =
        memberJpaPort.findMemberByRefreshToken(commend.token)
}