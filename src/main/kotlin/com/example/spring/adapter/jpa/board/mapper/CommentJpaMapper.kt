package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.adapter.jpa.board.repository.PostJpaRepository
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.domain.board.Comment
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CommentJpaMapper(
    private val boardJpaRepository: BoardJpaRepository,
    private val postJpaRepository: PostJpaRepository,
    private val commentJpaRepository: CommentJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {
    fun toComment(commentJpaEntity: CommentJpaEntity): Comment {
        return commentJpaEntity.let {
            Comment(
                commentId = it.commentId,
                boardId = it.board.boardId,
                postId = it.post.postId,
                parentComment = when (it.parentComment) {
                    null -> null
                    else -> it.parentComment!!.commentId
                },
                level = it.level,
                content = it.content,
                writer = it.writer.email
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
                up = it.up
                childCommentCount = commentJpaRepository.countByParentComment(it)
            }
        }
    }

    fun toJpaEntity(comment: Comment): CommentJpaEntity {
        return comment.let {
            CommentJpaEntity(
                commentId = it.commentId,
                board = boardJpaRepository.findByBoardId(it.boardId)!!,
                post = postJpaRepository.findByIdOrNull(it.postId)!!,
                parentComment = when (it.parentComment) {
                    null -> null
                    else -> commentJpaRepository.findByIdOrNull(it.parentComment)
                },
                level = it.level,
                content = it.content,
                writer = memberJpaRepository.findByEmail(it.writer)!!
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
                up = it.up
            }
        }
    }
}