package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.adapter.jpa.board.repository.PostJpaRepository
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.application.service.board.exception.BoardDataNotFoundException
import com.example.spring.application.service.board.exception.CommentDataNotFoundException
import com.example.spring.application.service.board.exception.PostDataNotFoundException
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
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
                like = it.like
                childCommentCount = commentJpaRepository.countByParentComment(it)
            }
        }
    }

    fun toJpaEntity(comment: Comment): CommentJpaEntity {
        return comment.let {
            CommentJpaEntity(
                commentId = it.commentId,
                board = boardJpaRepository.findByIdOrNull(it.boardId)
                    ?: throw BoardDataNotFoundException(boardId = it.boardId),
                post = postJpaRepository.findByIdOrNull(it.postId)
                    ?: throw PostDataNotFoundException(boardId = it.boardId, postId = it.postId),
                parentComment = when (it.parentComment) {
                    null -> null
                    else -> commentJpaRepository.findByIdOrNull(it.parentComment) ?: throw CommentDataNotFoundException(
                        boardId = it.boardId,
                        postId = it.postId,
                        commentId = it.commentId
                    )
                },
                level = it.level,
                content = it.content,
                writer = memberJpaRepository.findByEmail(it.writer) ?: throw MemberDataNotFoundException()
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
                like = it.like
            }
        }
    }
}