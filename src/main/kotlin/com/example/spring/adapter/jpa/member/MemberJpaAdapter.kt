package com.example.spring.adapter.jpa.member


import com.example.spring.adapter.jpa.member.entity.MemberEntity
import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.application.port.out.member.MemberPort
import com.example.spring.domain.member.Member
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MemberJpaAdapter(private val memberJpaRepository: MemberJpaRepository) : MemberPort {
    val memberJpaMapper = MemberJpaMapper.INSTANCE

    /**
     * ID,PW 확인
     * */
    override fun checkAuth(member: Member): Member? {
        val findMember: Member? = memberJpaMapper.toMember(memberJpaRepository.findById(member.id))

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
    override fun findMemberById(id: String): MemberEntity? {
        return memberJpaRepository.findById(id)
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
        val findMember: MemberEntity? = memberJpaRepository.findById(member.id)

        if (findMember == null) {
            return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))
        } else {
            return null
        }
    }

    @Transactional
    override fun saveRefreshToken(id: String, token: String) {
        memberJpaRepository.findById(id)!!.refreshToken = token
    }

    override fun findMemberByRefreshToken(token: String): MemberEntity {
        return memberJpaRepository.findByRefreshToken(token)
    }

}