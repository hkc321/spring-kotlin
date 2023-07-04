package com.example.spring.adapter.rest.board.mapper

import com.example.spring.adapter.rest.board.dto.PostCommonResponse
import com.example.spring.adapter.rest.board.dto.PostReadPageListResponse
import com.example.spring.domain.board.Post
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

@Mapper
interface PostRestMapper {
    companion object {
        val INSTANCE: PostRestMapper = Mappers.getMapper(PostRestMapper::class.java)
    }

    @Mappings(
        Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
    )
    fun toPostCommonResponse(post: Post): PostCommonResponse

    @Mappings(
        Mapping(source = "pageable.pageNumber", target = "currentPage")
    )
    fun toPostReadPageListResponse(pageList: Page<PostCommonResponse>): PostReadPageListResponse
}