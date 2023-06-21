package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.rest.board.dto.CommentResponse
import com.example.spring.domain.board.Comment
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface CommentJpaMapper {
    @Mapping(target = "childCommentCount", ignore = true)
    fun toComment(dto: CommentJpaEntity?): Comment

    fun toEntity(comment: Comment): CommentJpaEntity

    fun toCommentResponse(comment: Comment): CommentResponse

    companion object {
        val INSTANCE: CommentJpaMapper = Mappers.getMapper(CommentJpaMapper::class.java)
    }
}