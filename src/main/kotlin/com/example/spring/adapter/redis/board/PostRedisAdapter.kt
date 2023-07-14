package com.example.spring.adapter.redis.board

import com.example.spring.adapter.redis.board.repository.PostRedisRepository
import com.example.spring.application.port.out.board.PostRedisPort
import com.example.spring.application.service.board.exception.PostLikeException
import com.example.spring.config.code.ErrorCode
import org.springframework.stereotype.Repository

@Repository
class PostRedisAdapter(private val postRedisRepository: PostRedisRepository) : PostRedisPort {
    override fun createPostLike(boardId: Int, postId: Int, email: String): Int =
        when (postRedisRepository.createLikePost(boardId, postId, email)) {
            1L -> {
                postRedisRepository.countLikePost(boardId, postId).let { it!!.toInt() }
            }

            else -> throw PostLikeException(
                boardId = boardId,
                postId = postId,
                code = ErrorCode.ALREADY_EXIST,
                message = "이미 좋아요를 클릭한 게시글입니다. [boardId: $boardId, postId: $postId]"
            )
        }

    override fun countPostLike(boardId: Int, postId: Int): Int =
        postRedisRepository.countLikePost(boardId, postId).let { it!!.toInt() }

    override fun checkPostLikeByEmail(boardId: Int, postId: Int, email: String): Boolean =
        postRedisRepository.findLikePostByEmail(boardId, postId, email)!!

    override fun deletePostLike(boardId: Int, postId: Int, email: String): Int =
        when (postRedisRepository.deleteLikePost(boardId, postId, email)) {
            1L -> {
                postRedisRepository.countLikePost(boardId, postId).let { it!!.toInt() }
            }

            else -> throw PostLikeException(
                boardId = boardId,
                postId = postId,
                code = ErrorCode.DATA_NOT_FOUND,
                message = "좋아요를 클릭한 이력이 없습니다. [boardId: $boardId, postId: $postId]"
            )
        }

}