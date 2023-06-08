package com.example.spring.adapter.jpa.member.mapper

import com.example.spring.adapter.jpa.member.entity.MemberEntity
import com.example.spring.domain.member.Member
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface MemberJpaMapper {
    companion object {
        val INSTANCE: MemberJpaMapper = Mappers.getMapper(MemberJpaMapper::class.java)
    }

    @Mapping(target = "authStatus", ignore = true)
    fun toMember(dto: MemberEntity?): Member

    fun toEntity(member: Member): MemberEntity
}