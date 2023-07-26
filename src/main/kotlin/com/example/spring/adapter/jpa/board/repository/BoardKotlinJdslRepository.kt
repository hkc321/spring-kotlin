package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.repository.dto.BoardResult
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import jakarta.persistence.NoResultException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class BoardKotlinJdslRepository(private val queryFactory: SpringDataQueryFactory) {
    fun readBoard(boardId: Int): BoardResult? {
        try {
            return queryFactory.singleQuery<BoardResult> {
                selectMulti(
                    column(BoardJpaEntity::boardId),
                    column(BoardJpaEntity::name),
                    column(BoardJpaEntity::description),
                    column(BoardJpaEntity::createdAt),
                    column(BoardJpaEntity::updatedAt),
                    nestedCol(column(BoardJpaEntity::writer), MemberJpaEntity::email),
                    nestedCol(column(BoardJpaEntity::modifier), MemberJpaEntity::email),
                )
                from(entity(BoardJpaEntity::class))
                where(column(BoardJpaEntity::boardId).equal(boardId))
            }
        } catch (ex: NoResultException) {
            return null
        } catch (ex: Exception) {
            throw ex
        }
    }


    fun readBoardPageList(keyword: String?, searchType: String?, pageable: Pageable): Page<BoardResult> {
        try {
            return queryFactory.pageQuery<BoardResult>(pageable) {
                selectMulti(
                    column(BoardJpaEntity::boardId),
                    column(BoardJpaEntity::name),
                    column(BoardJpaEntity::description),
                    column(BoardJpaEntity::createdAt),
                    column(BoardJpaEntity::updatedAt),
                    nestedCol(column(BoardJpaEntity::writer), MemberJpaEntity::email),
                    nestedCol(column(BoardJpaEntity::modifier), MemberJpaEntity::email),
                )
                from(entity(BoardJpaEntity::class))
                where(
                    keyword?.let {
                        when (searchType) {
                            "content" -> column(BoardJpaEntity::name).like(it)
                            "description" -> column(BoardJpaEntity::description).like(it)
                            "writer" -> nestedCol(column(BoardJpaEntity::writer), MemberJpaEntity::email).like(it)
                            else -> null
                        }
                    }
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
    }
}