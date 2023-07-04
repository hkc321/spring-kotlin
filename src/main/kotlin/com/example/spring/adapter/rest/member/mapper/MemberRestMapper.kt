package com.example.spring.adapter.rest.member.mapper

import com.example.spring.adapter.rest.member.dto.MemberCommonResponse
import com.example.spring.domain.member.Member
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface MemberRestMapper {
    companion object {
        val INSTANCE: MemberRestMapper = Mappers.getMapper(MemberRestMapper::class.java)
    }

    fun toMemberCommonResponse(member: Member): MemberCommonResponse
}