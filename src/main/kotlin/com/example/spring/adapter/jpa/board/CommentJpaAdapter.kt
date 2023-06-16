package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.adapter.rest.board.dto.CommentReadBoardCommentTopLevelListResponse
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.config.NoDataException
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CommentJpaAdapter(
    private val commentJpaRepository: CommentJpaRepository
) : CommentJpaPort {
    val commentJpaMapper = CommentJpaMapper.INSTANCE

    override fun readBoardCommentTopLevelList(
        boardId: Int,
        pageable: Pageable
    ): CommentReadBoardCommentTopLevelListResponse {
        commentJpaRepository.findSliceByBoardIdAndLevel(boardId, pageable).map {
            commentJpaMapper.toComment(it).apply {
                this.childCommentCount =
                    commentJpaRepository.countByParentCommentIdAndCommentIdIsNot(it.parentCommentId, it.commentId)
            }
        }.apply {
            return CommentReadBoardCommentTopLevelListResponse(content, isLast, isEmpty)
        }
    }

    override fun createComment(comment: Comment): Comment =
        commentJpaMapper.toComment(commentJpaRepository.save(commentJpaMapper.toEntity(comment)))

    override fun readComment(commentId: Int): Comment {
        commentJpaRepository.findByIdOrNull(commentId)?.let {
            return commentJpaMapper.toComment(it)
        } ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }
}
