package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.adapter.jpa.board.repository.CommentKotlinJdslRepository
import com.example.spring.application.port.out.board.CommentKotlinJdslPort
import com.example.spring.domain.board.Comment
import org.springframework.stereotype.Repository

@Repository
class CommentKotlinJdslAdapter(
    private val commentKotlinJdslRepository: CommentKotlinJdslRepository,
    private val commentJpaMapper: CommentJpaMapper
) : CommentKotlinJdslPort {
    override fun readTopLevelComment(
        boardId: Int,
        postId: Int,
        size: Int,
        cursor: Int?,
        orderBy: String
    ): Pair<List<Comment>, Int?> {
        val comments = commentKotlinJdslRepository.readTopLevelComment(boardId, postId, size, cursor, orderBy).map {
            commentJpaMapper.toComment(it)
        }

        return calcLast(comments, size)
    }

    override fun readChildComment(
        boardId: Int,
        postId: Int,
        parentCommentId: Int,
        size: Int,
        cursor: Int?
    ): Pair<List<Comment>, Int?> {
        val comments =
            commentKotlinJdslRepository.readChildComment(boardId, postId, parentCommentId, size, cursor).map {
                commentJpaMapper.toComment(it)
            }

        return calcLast(comments, size)
    }

    fun calcLast(comments: List<Comment>, size: Int): Pair<List<Comment>, Int?> {
        var lastValue: Int? = null
        val mutalbleCommentList: MutableList<Comment> = comments.toMutableList()

        if (comments.size > size) {
            mutalbleCommentList.removeLast()
            lastValue = mutalbleCommentList.last().commentId
        }

        return Pair(mutalbleCommentList, lastValue ?: let { null })
    }
}