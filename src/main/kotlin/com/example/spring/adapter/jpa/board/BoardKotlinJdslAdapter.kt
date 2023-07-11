package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.application.port.out.board.BoardKotlinJdslPort
import com.example.spring.config.BoardDataNotFoundException
import com.example.spring.domain.board.Board
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.*
import jakarta.persistence.NoResultException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BoardKotlinJdslAdapter(
    private val queryFactory: SpringDataQueryFactory,
    private val boardJpaMapper: BoardJpaMapper
) : BoardKotlinJdslPort {
    override fun readBoard(boardId: Int): Board {
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
            }.let {
                boardJpaMapper.toBoard(it)
            }
        } catch (ex: NoResultException) {
            throw BoardDataNotFoundException(boardId = boardId)
        }
    }


    override fun readBoardPageList(keyword: String?, searchType: String?, pageable: Pageable): Page<Board> =
        queryFactory.pageQuery<BoardResult>(pageable) {
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
        }.map {
            boardJpaMapper.toBoard(it)
        }

    data class BoardResult(
        val boardId: Int,
        var name: String,
        var description: String,
        var createdAt: LocalDateTime,
        var updatedAt: LocalDateTime?,
        var writer: String,
        var modifier: String,
    )
}