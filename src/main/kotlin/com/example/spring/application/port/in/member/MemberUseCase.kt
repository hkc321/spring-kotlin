package com.example.spring.application.port.`in`.member

import com.example.spring.adapter.rest.member.dto.MemberCommonResponse
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.domain.member.Member
import com.example.spring.domain.member.MemberRole
import org.springframework.data.domain.Pageable

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
        abstract val email: String

        data class CreateCommend(
            override val email: String,
            val password: String,
            var role: MemberRole
        ) : Commend()

        data class ReadCommend(
            override val email: String
        ) : Commend()

        data class UpdateCommend(
            override val email: String,
            val password: String
        ) : Commend()

        data class DeleteCommend(
            override val email: String
        ) : Commend()

        data class SaveRefreshTokenCommend(
            override val email: String,
            val token: String
        ) : Commend()

        data class FindMemberByRefreshTokenCommend(
            val token: String
        )
    }
}