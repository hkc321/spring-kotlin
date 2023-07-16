package com.example.spring.adapter.rest.board.mapper

import com.example.spring.adapter.rest.board.dto.PostSingleResponse
import com.example.spring.domain.board.Post
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class PostSingleReponseMapper {
    fun toPostSingleResponse(post: Post): PostSingleResponse =
        PostSingleResponse(
            postId = post.postId,
            boardId = post.boardId,
            title = post.title,
            content = post.content,
            like = post.like,
            isLiked = post.isLiked,
            writer = post.writer,
            createdAt = post.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            updatedAt = post.updatedAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
}