package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.application.port.out.board.BoardKotlinJdslPort
import com.example.spring.domain.board.Board
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.associate
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class BoardKotlinJdslAdapter(private val queryFactory: SpringDataQueryFactory, private val boardJpaMapper: BoardJpaMapper) : BoardKotlinJdslPort {
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
        queryFactory.pageQuery<BoardJpaEntity>(pageable) {
            select(entity(BoardJpaEntity::class))
            from(entity(BoardJpaEntity::class))
            associate(BoardJpaEntity::writer)
            where(
                keyword?.let {
                    when (searchType) {
                        "content" -> column(BoardJpaEntity::name).like(it)
                        "description" -> column(BoardJpaEntity::description).like(it)
                        "writer" -> column(MemberJpaEntity::email).like(it)
                        else -> null
                    }
                }
            )
        }.map {
            boardJpaMapper.toBoard(it)
        }
}