package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.config.NoDataException
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CommentJpaAdapter(
    private val commentJpaRepository: CommentJpaRepository
) : CommentJpaPort {
    val commentJpaMapper = CommentJpaMapper.INSTANCE

    override fun createComment(comment: Comment): Comment =
        commentJpaMapper.toComment(commentJpaRepository.save(commentJpaMapper.toEntity(comment)))

    override fun readComment(commentId: Int): Comment {
        commentJpaRepository.findByIdOrNull(commentId)?.let {
            return commentJpaMapper.toComment(it)
        } ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }

    override fun readChildComment(commentId: Int, pageable: Pageable): Slice<Comment> {
        val pageRequest: Pageable = PageRequest.of(
            pageable.pageNumber,
            20,
            Sort.by("commentId").ascending()
        )
        return commentJpaRepository.findByParentCommentIdAndLevelGreaterThan(commentId, pageRequest).map {
            commentJpaMapper.toComment(it)
        }
    }

    @Transactional
    override fun updateComment(commentId: Int, comment: Comment): Comment {
        commentJpaRepository.findByIdOrNull(commentId)
            ?.let {
                it.updateComment(comment)

                return commentJpaMapper.toComment(it)
            }
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }
}
