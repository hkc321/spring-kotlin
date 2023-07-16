package com.example.spring.adapter.jpa.board.repository

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.repository.dto.PostPageResult
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.domain.board.Board
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PostKotlinJdslRepository(private val queryFactory: SpringDataQueryFactory) {

    fun readPost(board: Board, postId: Int): PostJpaEntity =
        queryFactory.singleQuery<PostJpaEntity> {
            select(entity(PostJpaEntity::class))
            from(entity(PostJpaEntity::class))
            fetch(PostJpaEntity::writer)
            where(column(PostJpaEntity::postId).equal(postId))
        }

    fun readPostPageList(boardId: Int, keyword: String?, searchType: String?, pageable: Pageable): Page<PostPageResult> =
        queryFactory.pageQuery<PostPageResult>(pageable) {
            selectMulti(
                column(PostJpaEntity::board),
                column(PostJpaEntity::postId),
                column(PostJpaEntity::title),
                column(PostJpaEntity::content),
                column(PostJpaEntity::like),
                nestedCol(column(PostJpaEntity::writer), MemberJpaEntity::email),
                column(PostJpaEntity::createdAt),
                column(PostJpaEntity::updatedAt)
            )
            from(entity(PostJpaEntity::class))
            whereAnd(
                nestedCol(column(PostJpaEntity::board), BoardJpaEntity::boardId).equal(boardId),
                keyword?.let {
                    when (searchType) {
                        "title" -> column(PostJpaEntity::title).like(it)
                        "content" -> column(PostJpaEntity::content).like(it)
                        "writer" -> nestedCol(column(PostJpaEntity::writer), MemberJpaEntity::email).like(it)
                        else -> null
                    }
                }
            )
        }
}