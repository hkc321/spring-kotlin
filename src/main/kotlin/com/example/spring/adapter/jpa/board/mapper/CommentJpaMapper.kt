package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.domain.board.Comment
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface CommentJpaMapper {
    fun toComment(dto: CommentJpaEntity?): Comment

    fun toEntity(comment: Comment): CommentJpaEntity

    companion object {
        val INSTANCE: CommentJpaMapper = Mappers.getMapper(CommentJpaMapper::class.java)
    }
}