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
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import jakarta.persistence.NoResultException
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
        val cursorComment: CommentJpaEntity? = cursor?.let {
            try {
                queryFactory.singleQuery<CommentJpaEntity?> {
                    select(entity(CommentJpaEntity::class))
                    from(entity(CommentJpaEntity::class))
                    where(column(CommentJpaEntity::commentId).equal(cursor))
                }
            } catch (ex: NoResultException) {
                null
            }
        }

        var comments: List<Comment> = queryFactory.listQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            fetch(CommentJpaEntity::writer)
            associate(CommentJpaEntity::board)
            associate(CommentJpaEntity::post)
            whereAnd(
                column(BoardJpaEntity::boardId).equal(boardId),
                column(PostJpaEntity::postId).equal(postId),
                column(CommentJpaEntity::parentComment).isNull(),
            )
            // where
            cursorComment?.let {
                if (orderBy == "up") {
                    whereOr(
                        and(
                            column(CommentJpaEntity::up).equal(it.up),
                            column(CommentJpaEntity::commentId).lessThan(it.commentId)
                        ),
                        column(CommentJpaEntity::up).lessThan(it.up)
                    )
                } else {
                    whereAnd(
                        column(CommentJpaEntity::commentId).lessThan(it.commentId)
                    )
                }
            } ?: cursor?.let{whereAnd(column(CommentJpaEntity::commentId).lessThan(0))} // 존재하지 않는 최상위 게시글을 커서로 넘긴 경우 빈 리스트 반환되도록 설정}


            //orderby
            if (orderBy == "up") {
                orderBy(
                    ExpressionOrderSpec(column(CommentJpaEntity::up), false),
                    ExpressionOrderSpec(column(CommentJpaEntity::commentId), false)
                )
            } else {
                orderBy(ExpressionOrderSpec(column(CommentJpaEntity::commentId), false))
            }
            limit(size + 1)
        }.map {
            commentJpaMapper.toComment(it)
        }

        var lastValue: Int? = null
        if (comments.size > size) {
            comments = comments.toMutableList()
            comments.removeLast()
            lastValue = comments.last().commentId
        }

        return Pair(comments, lastValue ?: let { null })
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

        var comments: List<Comment> = queryFactory.listQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            fetch(CommentJpaEntity::writer)
            associate(CommentJpaEntity::board)
            associate(CommentJpaEntity::post)
            whereAnd(
                column(BoardJpaEntity::boardId).equal(boardId),
                column(PostJpaEntity::postId).equal(postId),
                column(CommentJpaEntity::parentComment).equal(parentComment),
                cursor?.let {
                    column(CommentJpaEntity::commentId).greaterThan(cursor)
                }
            )
            limit(size + 1)
        }.map {
            commentJpaMapper.toComment(it)
        }

        var lastValue: Int? = null
        if (comments.size > size) {
            comments = comments.toMutableList()
            comments.removeLast()
            lastValue = comments.last().commentId
        }

        return Pair(comments, lastValue ?: let { null })
    }
}