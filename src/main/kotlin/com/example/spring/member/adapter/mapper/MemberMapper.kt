package com.example.spring.member.adapter.mapper

import com.example.spring.member.adapter.out.persistence.MemberEntity
import com.example.spring.member.domain.Member
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface MemberMapper {
    companion object {
        val INSTANCE: MemberMapper = Mappers.getMapper(MemberMapper::class.java)
    }

    @Mapping(target = "authStatus", ignore = true)
    fun toMember(dto: MemberEntity?): Member?

    fun toEntity(member: Member): MemberEntity
}