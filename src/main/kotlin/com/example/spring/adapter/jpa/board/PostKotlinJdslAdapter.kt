package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.mapper.PostJpaMapper
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.domain.board.Post
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.associate
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PostKotlinJdslAdapter(
    private val queryFactory: SpringDataQueryFactory,
    private val postJpaMapper: PostJpaMapper
) : PostKotlinJdslPort {
    override fun readPostPageList(boardId: Int, keyword: String?, searchType: String?, pageable: Pageable): Page<Post> {
        return queryFactory.pageQuery<PostJpaEntity>(pageable) {
            select(entity(PostJpaEntity::class))
            from(entity(PostJpaEntity::class))
            associate(PostJpaEntity::board)
            associate(PostJpaEntity::writer)
            whereAnd(
                column(BoardJpaEntity::boardId).equal(boardId),
                keyword?.let {
                    when (searchType) {
                        "title" -> column(BoardJpaEntity::name).like(it)
                        "content" -> column(BoardJpaEntity::description).like(it)
                        "writer" -> column(MemberJpaEntity::email).like(it)
                        else -> null
                    }
                }
            )
        }.map {
            postJpaMapper.toPost(it)
        }
    }
}