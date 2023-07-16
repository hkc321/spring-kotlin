package com.example.spring.adapter.redis.board

import com.example.spring.adapter.redis.board.repository.CommentRedisRepository
import com.example.spring.application.port.out.board.CommentRedisPort
import com.example.spring.application.service.board.exception.CommentLikeException
import com.example.spring.config.code.ErrorCode
import org.springframework.stereotype.Repository

@Repository
class CommentRedisAdapter(private val commentRedisRepository: CommentRedisRepository) : CommentRedisPort {
    override fun createCommentLike(boardId: Int, postId: Int, commentId: Int, email: String): Int =
        when (commentRedisRepository.createLikeComment(boardId, postId, commentId, email)) {
            1L -> {
                commentRedisRepository.countLikeComment(boardId, postId, commentId).let { it!!.toInt() }
            }

            else -> throw CommentLikeException(
                boardId = boardId,
                postId = postId,
                commentId = commentId,
                code = ErrorCode.ALREADY_EXIST,
                message = "이미 좋아요를 클릭한 댓글입니다. [boardId: $boardId, postId: $postId, commentId: $commentId]"
            )
        }

    override fun countCommentLike(boardId: Int, postId: Int, commentId: Int): Int =
        commentRedisRepository.countLikeComment(boardId, postId, commentId).let { it!!.toInt() }

    override fun checkCommentLikeByEmail(boardId: Int, postId: Int, commentId: Int, email: String): Boolean =
        commentRedisRepository.findLikeCommentByEmail(boardId, postId, commentId, email)!!

    override fun deleteCommentLike(boardId: Int, postId: Int, commentId: Int, email: String): Int =
        when (commentRedisRepository.deleteLikeComment(boardId, postId, commentId, email)) {
            1L -> {
                commentRedisRepository.countLikeComment(boardId, postId, commentId).let { it!!.toInt() }
            }

            else -> throw CommentLikeException(
                boardId = boardId,
                postId = postId,
                commentId = commentId,
                code = ErrorCode.DATA_NOT_FOUND,
                message = "좋아요를 클릭한 이력이 없습니다. [boardId: $boardId, postId: $postId, commentId: $commentId]"
            )
        }
}