package com.example.spring.member.adapter.out.persistence


import com.example.spring.member.adapter.mapper.MemberMapper
import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.domain.Auth
import com.example.spring.member.domain.Member
import org.springframework.stereotype.Repository

@Repository
class MemberAdapter(private val memberRepository: MemberRepository): MemberPort {
    val memberMapper = MemberMapper.INSTANCE

    /**
     * ID,PW 확인
     * */
    override fun checkAuth(auth: Auth): Member? {
        val member: Member? = memberMapper.toMember(memberRepository.findByMemId(auth.memId))

        if (member != null){
            if(!member.comparePW(auth.memPw)){
                member.authStatus = Member.Status.WRONG_PW
            }else{
                member.authStatus = Member.Status.AUTHENTIC
            }
            return member
        }
        return null
    }

    /**
     * Member 찾기
     * */
    override fun findMember() {
        TODO("Not yet implemented")
    }

    /**
     * Member 등록
     * */
    override fun registerMember() {
        TODO("Not yet implemented")
    }
}