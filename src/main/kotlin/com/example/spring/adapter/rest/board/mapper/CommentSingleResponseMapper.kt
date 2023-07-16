package com.example.spring.adapter.rest.board.mapper

import com.example.spring.adapter.rest.board.dto.CommentSingleResponse
import com.example.spring.domain.board.Comment
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class CommentSingleResponseMapper{
    fun toCommentSingleResponse(comment: Comment) : CommentSingleResponse =
        CommentSingleResponse(
            boardId = comment.boardId,
            postId = comment.postId,
            commentId = comment.commentId,
            parentCommentId = comment.parentComment,
            level = comment.level,
            like = comment.like,
            isLiked = comment.isLiked,
            content = comment.content,
            childCommentCount = comment.childCommentCount,
            writer = comment.writer,
            createdAt = comment.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            updatedAt = comment.updatedAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
}
