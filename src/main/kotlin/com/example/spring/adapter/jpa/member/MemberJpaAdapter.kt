package com.example.spring.adapter.jpa.member


import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.config.MemberAlreadyExistException
import com.example.spring.config.MemberDataNotFoundException
import com.example.spring.domain.member.Member
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MemberJpaAdapter(private val memberJpaRepository: MemberJpaRepository) : MemberJpaPort {
    val memberJpaMapper = MemberJpaMapper.INSTANCE

    /**
     * Member 찾기
     * */
    override fun findMemberByEmail(email: String): Member =
        memberJpaRepository.findByEmail(email)
            ?.let {
                memberJpaMapper.toMember(it)
            } ?: throw MemberDataNotFoundException()

    /**
     * Member 찾기
     * */
    override fun findMemberByMemberId(memberId: Int): Member? {
        memberJpaRepository.findByMemberId(memberId)?.let {
            return memberJpaMapper.toMember(it)
        } ?: throw MemberDataNotFoundException()
    }

    /**
     * Member 등록
     * */
    override fun createMember(member: Member): Member =
        memberJpaRepository.findByEmail(member.email)
            ?.run {
                throw MemberAlreadyExistException()
            } ?: run {
            memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))
        }

    override fun updateMember(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))

    override fun deleteMember(email: String) =
        memberJpaRepository.findByEmail(email)
            ?.let {
                memberJpaRepository.deleteById(it.memberId)
            } ?: throw MemberDataNotFoundException()

    override fun saveRefreshToken(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))

    override fun findMemberByRefreshToken(token: String): Member =
        memberJpaRepository.findByRefreshToken(token)
            ?.let {
                memberJpaMapper.toMember(it)
            } ?: throw MemberDataNotFoundException()

}