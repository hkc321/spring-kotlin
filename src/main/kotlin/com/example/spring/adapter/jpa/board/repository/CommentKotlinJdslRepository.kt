package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.CommentJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.linecorp.kotlinjdsl.querydsl.expression.column
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
            val whereAdditional = cursorComment?.run {
                if (orderBy == "like") {
                    whereOr(
                        and(
                            column(CommentJpaEntity::like).equal(this.like),
                            column(CommentJpaEntity::commentId).lessThan(this.commentId)
                        ),
                        column(CommentJpaEntity::like).lessThan(this.like)
                    )
                } else {
                    whereAnd(
                        column(CommentJpaEntity::commentId).lessThan(this.commentId)
                    )
                }
            } ?: cursor?.run { whereAnd(column(CommentJpaEntity::commentId).lessThan(0)) } // 존재하지 않는 최상위 게시글을 커서로 넘긴 경우 빈 리스트 반환되도록 설정}

            val orderBySpec = if (orderBy == "like") {
                orderBy(
                    column(CommentJpaEntity::like).desc(),
                    column(CommentJpaEntity::commentId).desc()
                )
            } else {
                orderBy(column(CommentJpaEntity::commentId).desc())
            }

            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            fetch(CommentJpaEntity::writer)
            whereAnd(
                nestedCol(column(CommentJpaEntity::board),BoardJpaEntity::boardId).equal(boardId),
                nestedCol(column(CommentJpaEntity::post),PostJpaEntity::postId).equal(postId),
                column(CommentJpaEntity::parentComment).isNull(),
            )
            whereAdditional
            orderBySpec
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
        val parentComment: CommentJpaEntity? =
            try {
                queryFactory.singleQuery<CommentJpaEntity?> {
                    select(entity(CommentJpaEntity::class))
                    from(entity(CommentJpaEntity::class))
                    where(
                        column(CommentJpaEntity::commentId).equal(parentCommentId)
                    )
                }
            } catch (ex: NoResultException) {
                null
            }

        return queryFactory.listQuery<CommentJpaEntity> {
            select(entity(CommentJpaEntity::class))
            from(entity(CommentJpaEntity::class))
            fetch(CommentJpaEntity::writer)
            whereAnd(
                nestedCol(column(CommentJpaEntity::board),BoardJpaEntity::boardId).equal(boardId),
                nestedCol(column(CommentJpaEntity::post),PostJpaEntity::postId).equal(postId),
                column(CommentJpaEntity::parentComment).equal(parentComment),
                cursor?.let {
                    column(CommentJpaEntity::commentId).greaterThan(cursor)
                }
            )
            orderBy(column(CommentJpaEntity::commentId).asc())
            limit(size + 1)
        }
    }
}