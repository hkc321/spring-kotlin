package com.example.spring.application.port.`in`.member

import com.example.spring.domain.member.Member
import com.example.spring.domain.member.MemberRole

interface MemberUseCase {
    /**
     * Member 등록
     * */
    fun createMember(commend: Commend.CreateCommend): Member

    /**
     * Member 찾기
     * */
    fun readMember(commend: Commend.ReadCommend): Member

    /**
     * Member 수정
     * */
    fun updateMember(commend: Commend.UpdateCommend): Member

    /**
     * Member 제거
     * */
    fun deleteMember(commend: Commend.DeleteCommend)

    /**
     * 로그아웃
     * */
    fun logout()

    /**
     * Refresh 토큰 저장
     * */
    fun saveRefreshToken(commend: Commend.SaveRefreshTokenCommend): Member

    /**
     * Member 찾기
     * */
    fun findMemberByRefreshToken(commend: Commend.FindMemberByRefreshTokenCommend): Member

    sealed class Commend {
        data class CreateCommend(
            val email: String,
            val password: String,
            var role: MemberRole
        )

        data class ReadCommend(
            val memberId: Int,
            val accessor: String
        ) : Commend()

        data class UpdateCommend(
            val memberId: Int,
            val password: String,
            val accessor: String
        ) : Commend()

        data class DeleteCommend(
            val memberId: Int,
            val accessor: String
        ) : Commend()

        data class SaveRefreshTokenCommend(
            val email: String,
            val token: String
        ) : Commend()

        data class FindMemberByRefreshTokenCommend(
            val token: String
        ) : Commend()
    }
}