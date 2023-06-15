package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.adapter.rest.board.dto.CommentResponse
import com.example.spring.application.port.out.board.CommentJpaPort
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CommentJpaAdapter(
    private val commentJpaRepository: CommentJpaRepository
) : CommentJpaPort {
    val commentJpaMapper = CommentJpaMapper.INSTANCE

    override fun readBoardCommentTopLevelList(boardId: Int, pageable: Pageable): CommentResponse {
        commentJpaRepository.findSliceByBoardIdAndLevel(boardId, pageable).map {
            commentJpaMapper.toComment(it)
        }.apply {
            return CommentResponse(content, isLast, isEmpty)
        }
    }
}
