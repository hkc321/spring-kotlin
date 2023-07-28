package com.example.spring.adapter.jpa.member


import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.domain.member.Member
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MemberJpaAdapter(private val memberJpaRepository: MemberJpaRepository) : MemberJpaPort {
    val memberJpaMapper = MemberJpaMapper.INSTANCE

    /**
     * Member 찾기
     * */
    override fun findMemberByEmail(email: String): Member? =
        memberJpaRepository.findByEmail(email)?.let {
                memberJpaMapper.toMember(it)
            }

    /**
     * Member 찾기
     * */
    override fun findMemberByMemberId(memberId: Int): Member? =
        memberJpaRepository.findByIdOrNull(memberId)?.let {
            memberJpaMapper.toMember(it)
        }

    /**
     * Member 등록
     * */
    override fun createMember(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.saveAndFlush(memberJpaMapper.toEntity(member)))

    override fun updateMember(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))

    override fun updateMemberRole(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))

    override fun deleteMember(memberId: Int) =
        memberJpaRepository.deleteById(memberId)

}