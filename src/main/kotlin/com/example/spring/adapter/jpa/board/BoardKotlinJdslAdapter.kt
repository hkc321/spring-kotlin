package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.application.port.out.board.BoardKotlinJdslPort
import com.example.spring.domain.board.Board
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.spring.data.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BoardKotlinJdslAdapter(
    private val queryFactory: SpringDataQueryFactory,
    private val boardJpaMapper: BoardJpaMapper
) : BoardKotlinJdslPort {
    override fun readBoard(boardId: Int): Board =
        queryFactory.singleQuery<BoardJpaEntity> {
            select(entity(BoardJpaEntity::class))
            from(entity(BoardJpaEntity::class))
            fetch(BoardJpaEntity::writer)
            where(column(BoardJpaEntity::boardId).equal(boardId))
        }.let {
            boardJpaMapper.toBoard(it)
        }


    override fun readBoardPageList(keyword: String?, searchType: String?, pageable: Pageable): Page<Board> =
        queryFactory.pageQuery<BoardPageResult>(pageable) {
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

    data class BoardPageResult(
        val boardId: Int,
        var name: String,
        var description: String,
        var createdAt: LocalDateTime,
        var updatedAt: LocalDateTime?,
        var writer: String,
        var modifier: String,
    )
}