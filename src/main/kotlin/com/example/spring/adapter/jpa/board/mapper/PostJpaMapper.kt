package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.PostKotlinJdslAdapter
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.config.controller.BoardDataNotFoundException
import com.example.spring.config.controller.MemberDataNotFoundException
import com.example.spring.domain.board.Post
import org.springframework.stereotype.Repository

@Repository
class PostJpaMapper(
    private val boardJpaRepository: BoardJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {
    fun toPost(postJpaEntity: PostJpaEntity): Post {
        return postJpaEntity.let {
            Post(
                postId = it.postId,
                boardId = it.board.boardId,
                title = it.title,
                content = it.content,
                writer = it.writer.email
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }

    fun toPost(postPageResult: PostKotlinJdslAdapter.PostPageResult): Post {
        return postPageResult.let {
            Post(
                postId = it.postId,
                boardId = it.board.boardId,
                title = it.title,
                content = it.content,
                writer = it.writer
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }

    fun toJpaEntity(post: Post): PostJpaEntity {
        return post.let {
            PostJpaEntity(
                postId = it.postId,
                board = boardJpaRepository.findByBoardId(it.boardId) ?: throw BoardDataNotFoundException(boardId = it.boardId),
                title = it.title,
                content = it.content,
                writer = memberJpaRepository.findByEmail(it.writer) ?: throw MemberDataNotFoundException()
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }
}