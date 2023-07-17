package com.example.spring.adapter.rest.member.mapper

import com.example.spring.adapter.rest.member.dto.MemberCommonResponse
import com.example.spring.domain.member.Member
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

@Mapper
interface MemberRestMapper {
    companion object {
        val INSTANCE: MemberRestMapper = Mappers.getMapper(MemberRestMapper::class.java)
    }
    @Mappings(
        Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    )
    fun toMemberCommonResponse(member: Member): MemberCommonResponse
}