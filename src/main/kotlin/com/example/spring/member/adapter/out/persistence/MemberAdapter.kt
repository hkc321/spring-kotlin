package com.example.spring.member.adapter.out.persistence


import com.example.spring.member.adapter.mapper.MemberMapper
import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.domain.Member
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MemberAdapter(private val memberRepository: MemberRepository): MemberPort {
    val memberMapper = MemberMapper.INSTANCE

    /**
     * ID,PW 확인
     * */
    override fun checkAuth(member: Member): Member? {
        val findMember: Member? = memberMapper.toMember(memberRepository.findById(member.id))

        if (findMember != null){
            if(!findMember.comparePW(member.pw)){
                findMember.authStatus = Member.Status.WRONG_PW
            }else{
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
        return  memberRepository.findById(id)
    }

    /**
     * Member 찾기
     * */
    override fun findMemberByMemberId(memberId: Int): Member? {
        return  memberMapper.toMember(memberRepository.findByMemberId(memberId))
    }

    /**
     * Member 등록
     * */
    override fun registerMember(member: Member): Member? {
        // 아이디 있으면 가입 금지
        val findMember: MemberEntity? = memberRepository.findById(member.id)

        if (findMember == null){
            return memberMapper.toMember(memberRepository.save(memberMapper.toEntity(member)))
        }else{
            return null
        }
    }
    @Transactional
    override fun saveRefreshToken(id: String, token: String) {
        memberRepository.findById(id)!!.refreshToken = token
    }

    override fun findMemberByRefreshToken(token: String): MemberEntity {
        return memberRepository.findByRefreshToken(token)
    }

}