package com.example.spring.adapter.rest.board.mapper

import com.example.spring.adapter.rest.board.dto.CommentCommonResponse
import com.example.spring.domain.board.Comment
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

@Mapper
interface CommentRestMapper {
    companion object {
        val INSTANCE: CommentRestMapper = Mappers.getMapper(CommentRestMapper::class.java)
    }

    @Mappings(
        Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        Mapping(source = "parentComment", target = "parentCommentId")
    )
    fun toCommentCommonResponse(comment: Comment): CommentCommonResponse

}