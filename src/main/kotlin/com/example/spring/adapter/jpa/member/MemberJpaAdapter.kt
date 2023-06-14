package com.example.spring.adapter.jpa.member


import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.domain.member.Member
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MemberJpaAdapter(private val memberJpaRepository: MemberJpaRepository) : MemberJpaPort {
    val memberJpaMapper = MemberJpaMapper.INSTANCE

    /**
     * ID,PW 확인
     * */
    override fun checkAuth(member: Member): Member? {
        val findMember: Member = memberJpaMapper.toMember(memberJpaRepository.findByEmail(member.email))

        if (findMember != null) {
            if (!findMember.comparePW(member.pw)) {
                findMember.authStatus = Member.Status.WRONG_PW
            } else {
                findMember.authStatus = Member.Status.AUTHENTIC
            }
            return findMember
        }
        return null
    }

    /**
     * Member 찾기
     * */
    override fun findMemberByEmail(email: String): MemberJpaEntity? {
        return memberJpaRepository.findByEmail(email)
    }

    /**
     * Member 찾기
     * */
    override fun findMemberByMemberId(memberId: Int): Member? {
        return memberJpaMapper.toMember(memberJpaRepository.findByMemberId(memberId))
    }

    /**
     * Member 등록
     * */
    override fun registerMember(member: Member): Member? {
        // 아이디 있으면 가입 금지
        val findMember: MemberJpaEntity? = memberJpaRepository.findByEmail(member.email)

        if (findMember == null) {
            return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))
        } else {
            return null
        }
    }

    @Transactional
    override fun saveRefreshToken(id: String, token: String) {
        memberJpaRepository.findByEmail(id)!!.refreshToken = token
    }

    override fun findMemberByRefreshToken(token: String): MemberJpaEntity {
        return memberJpaRepository.findByRefreshToken(token)
    }

}