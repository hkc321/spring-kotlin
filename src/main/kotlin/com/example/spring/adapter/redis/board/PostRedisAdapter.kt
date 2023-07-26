package com.example.spring.adapter.redis.board

import com.example.spring.adapter.redis.board.repository.PostRedisRepository
import com.example.spring.application.port.out.board.PostRedisPort
import org.springframework.stereotype.Repository

@Repository
class PostRedisAdapter(private val postRedisRepository: PostRedisRepository) : PostRedisPort {
    override fun createPostLike(boardId: Int, postId: Int, email: String): Int? =
        when (postRedisRepository.createLikePost(boardId, postId, email)) {
            1L -> {
                postRedisRepository.countLikePost(boardId, postId).let { it!!.toInt() }
            }

            else -> null
        }

    override fun countPostLike(boardId: Int, postId: Int): Int =
        postRedisRepository.countLikePost(boardId, postId).let { it!!.toInt() }

    override fun checkPostLikeByEmail(boardId: Int, postId: Int, email: String): Boolean =
        postRedisRepository.findLikePostByEmail(boardId, postId, email)!!

    override fun deletePostLike(boardId: Int, postId: Int, email: String): Int? =
        when (postRedisRepository.deleteLikePost(boardId, postId, email)) {
            1L -> {
                postRedisRepository.countLikePost(boardId, postId).let { it!!.toInt() }
            }

            else -> null
        }

}