package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.config.CommentDataNotFoundException
import com.example.spring.domain.board.Comment
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CommentJpaAdapter(
    private val commentJpaRepository: CommentJpaRepository,
    private val commentJpaMapper: CommentJpaMapper
) : CommentJpaPort {

    override fun createComment(comment: Comment): Comment =
        commentJpaMapper.toComment(commentJpaRepository.save(commentJpaMapper.toJpaEntity(comment)))

    override fun readComment(boardId: Int, postId: Int, commentId: Int): Comment =
        commentJpaRepository.findByIdOrNull(
            commentId
        )?.let {
            return commentJpaMapper.toComment(it)
        } ?: throw CommentDataNotFoundException(boardId = boardId, postId = postId, commentId = commentId)


    override fun updateComment(comment: Comment): Comment =
        commentJpaMapper.toComment(commentJpaRepository.save(commentJpaMapper.toJpaEntity(comment)))

    override fun deleteComment(boardId: Int, postId: Int, commentId: Int) {
        commentJpaRepository.findByIdOrNull(commentId)?.let {
            commentJpaRepository.deleteById(commentId)
        } ?: throw CommentDataNotFoundException(boardId = boardId, postId = postId, commentId = commentId)
    }

}