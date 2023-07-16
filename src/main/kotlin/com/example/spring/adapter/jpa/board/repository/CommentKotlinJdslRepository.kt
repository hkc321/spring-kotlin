package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
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
class CommentKotlinJdslRepository(private val queryFactory: SpringDataQueryFactory) {
    fun readTopLevelComment(
        boardId: Int,
        postId: Int,
        size: Int,
        cursor: Int?,
        orderBy: String
    ): List<CommentJpaEntity> {
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

        return queryFactory.listQuery<CommentJpaEntity> {
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
                if (orderBy == "like") {
                    whereOr(
                        and(
                            column(CommentJpaEntity::like).equal(it.like),
                            column(CommentJpaEntity::commentId).lessThan(it.commentId)
                        ),
                        column(CommentJpaEntity::like).lessThan(it.like)
                    )
                } else {
                    whereAnd(
                        column(CommentJpaEntity::commentId).lessThan(it.commentId)
                    )
                }
            }
                ?: cursor?.let { whereAnd(column(CommentJpaEntity::commentId).lessThan(0)) } // 존재하지 않는 최상위 게시글을 커서로 넘긴 경우 빈 리스트 반환되도록 설정}


            //orderby
            if (orderBy == "like") {
                orderBy(
                    ExpressionOrderSpec(column(CommentJpaEntity::like), false),
                    ExpressionOrderSpec(column(CommentJpaEntity::commentId), false)
                )
            } else {
                orderBy(ExpressionOrderSpec(column(CommentJpaEntity::commentId), false))
            }
            limit(size + 1)
        }
    }

    fun readChildComment(
        boardId: Int,
        postId: Int,
        parentCommentId: Int,
        size: Int,
        cursor: Int?
    ): List<CommentJpaEntity> {
        val parentComment = queryFactory.singleQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            where(
                column(CommentJpaEntity::commentId).equal(parentCommentId)
            )
        }

        return queryFactory.listQuery<CommentJpaEntity> {
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
            orderBy(ExpressionOrderSpec(column(CommentJpaEntity::commentId), true))
            limit(size + 1)
        }
    }
}