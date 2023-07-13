package com.example.spring.adapter.jpa.member


import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.exception.MemberAlreadyExistException
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.domain.member.Member
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

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
    override fun findMemberByMemberId(memberId: Int): Member {
        memberJpaRepository.findByIdOrNull(memberId)?.let {
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

    override fun updateMemberRole(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))

    override fun deleteMember(memberId: Int) =
        memberJpaRepository.deleteById(memberId)

    override fun saveRefreshToken(member: Member): Member =
        memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toEntity(member)))

    override fun findMemberByRefreshToken(token: String): Member =
        memberJpaRepository.findByRefreshToken(token)
            ?.let {
                memberJpaMapper.toMember(it)
            } ?: throw MemberDataNotFoundException()

}