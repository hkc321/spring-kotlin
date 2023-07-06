package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
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

    fun toJpaEntity(post: Post): PostJpaEntity {
        return post.let {
            PostJpaEntity(
                postId = it.postId,
                board = boardJpaRepository.findByBoardId(it.boardId)!!,
                title = it.title,
                content = it.content,
                writer = memberJpaRepository.findByEmail(it.writer)!!
            ).apply {
                createdAt = it.createdAt
                updatedAt = it.updatedAt
            }
        }
    }
}