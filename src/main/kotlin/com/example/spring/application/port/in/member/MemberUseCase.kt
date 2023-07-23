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
     * Member Role 수정
     * */
    fun updateMemberRole(commend: Commend.UpdateRoleCommend): Member

    /**
     * Member 제거
     * */
    fun deleteMember(commend: Commend.DeleteCommend)

    sealed class Commend {
        data class CreateCommend(
            val email: String,
            val password: String
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

        data class UpdateRoleCommend(
            val memberId: Int,
            val role: MemberRole
        ) : Commend()

        data class DeleteCommend(
            val memberId: Int,
            val accessor: String
        ) : Commend()

    }
}