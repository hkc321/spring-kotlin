package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.application.port.out.board.CommentKotlinJdslPort
import com.example.spring.domain.board.Comment
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.associate
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.springframework.stereotype.Repository

@Repository
class CommentKotlinJdslAdapter(
    private val queryFactory: SpringDataQueryFactory,
    private val commentJpaMapper: CommentJpaMapper
) : CommentKotlinJdslPort {
    override fun readTopLevelComment(
        boardId: Int,
        postId: Int,
        size: Int,
        cursor: Int?,
        orderBy: String
    ): Pair<List<Comment>, Int?> {
        val comments: List<Comment> = queryFactory.listQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            associate(CommentJpaEntity::board)
            associate(CommentJpaEntity::post)
            whereAnd(
                column(BoardJpaEntity::boardId).equal(boardId),
                column(PostJpaEntity::postId).equal(postId),
                column(CommentJpaEntity::parentComment).isNull(),
                cursor?.let {
                    column(CommentJpaEntity::commentId).lessThan(cursor)
                }
            )
            orderBy(
                when (orderBy) {
                    "up" -> ExpressionOrderSpec(column(CommentJpaEntity::up), false)
                    else -> ExpressionOrderSpec(column(CommentJpaEntity::commentId), false)
                }
            )
            limit(size)
        }.map {
            commentJpaMapper.toComment(it)
        }

        val lastCommentId: Int? = when (comments.isNotEmpty()) {
            true -> comments.first().commentId
            else -> null
        }

        val nextCursor: Int? = when (lastCommentId != null) {
            true ->
                queryFactory.singleQuery<Int?> {
                    select(column(CommentJpaEntity::commentId))
                    from(entity(CommentJpaEntity::class))
                    associate(CommentJpaEntity::board)
                    associate(CommentJpaEntity::post)
                    whereAnd(
                        column(BoardJpaEntity::boardId).equal(boardId),
                        column(PostJpaEntity::postId).equal(postId),
                        column(CommentJpaEntity::parentComment).isNull(),
                        column(CommentJpaEntity::commentId).lessThan(lastCommentId)
                    )
                    limit(1)
                }

            else -> null
        }

        return Pair(comments, nextCursor?.let { lastCommentId } ?: let { null })
    }

    override fun readChildComment(
        boardId: Int,
        postId: Int,
        parentCommentId: Int,
        size: Int,
        cursor: Int?
    ): Pair<List<Comment>, Int?> {
        val parentComment = queryFactory.singleQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            where(
                column(CommentJpaEntity::commentId).equal(parentCommentId)
            )
        }

        val comments: List<Comment> = queryFactory.listQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            associate(CommentJpaEntity::board)
            associate(CommentJpaEntity::post)
            whereAnd(
                column(BoardJpaEntity::boardId).equal(boardId),
                column(PostJpaEntity::postId).equal(postId),
                column(CommentJpaEntity::parentComment).equal(parentComment),
                cursor?.let {
                    column(CommentJpaEntity::commentId).lessThan(cursor)
                }
            )
            limit(size)
        }.map {
            commentJpaMapper.toComment(it)
        }

        val lastCommentId: Int? = when (comments.isNotEmpty()) {
            true -> comments.last().commentId
            else -> null
        }

        val nextCursor: Int? = when (lastCommentId != null) {
            true ->
                queryFactory.singleQuery<Int?> {
                    select(column(CommentJpaEntity::commentId))
                    from(entity(CommentJpaEntity::class))
                    associate(CommentJpaEntity::board)
                    associate(CommentJpaEntity::post)
                    whereAnd(
                        column(BoardJpaEntity::boardId).equal(boardId),
                        column(PostJpaEntity::postId).equal(postId),
                        column(CommentJpaEntity::parentComment).equal(parentComment),
                        column(CommentJpaEntity::commentId).lessThan(lastCommentId)
                    )
                    limit(1)
                }

            else -> null
        }

        return Pair(comments, nextCursor?.let { lastCommentId } ?: let { null })
    }
}