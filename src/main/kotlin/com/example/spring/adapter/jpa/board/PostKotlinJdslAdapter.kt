package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.mapper.PostJpaMapper
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.config.controller.PostDataNotFoundException
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import jakarta.persistence.NoResultException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PostKotlinJdslAdapter(
    private val queryFactory: SpringDataQueryFactory,
    private val postJpaMapper: PostJpaMapper
) : PostKotlinJdslPort {
    override fun readPost(board: Board, postId: Int): Post {
        try {
            return queryFactory.singleQuery<PostJpaEntity> {
                select(entity(PostJpaEntity::class))
                from(entity(PostJpaEntity::class))
                fetch(PostJpaEntity::writer)
                where(column(PostJpaEntity::postId).equal(postId))
            }.let {
                postJpaMapper.toPost(it)
            }

        } catch (ex: NoResultException) {
            throw PostDataNotFoundException(boardId = board.boardId, postId = postId)
        }
    }

    override fun readPostPageList(boardId: Int, keyword: String?, searchType: String?, pageable: Pageable): Page<Post> {
        return queryFactory.pageQuery<PostPageResult>(pageable) {
            selectMulti(
                column(PostJpaEntity::board),
                column(PostJpaEntity::postId),
                column(PostJpaEntity::title),
                column(PostJpaEntity::content),
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
        }.map {
            postJpaMapper.toPost(it)
        }
    }

    data class PostPageResult(
        val board: BoardJpaEntity,
        val postId: Int,
        val title: String,
        val content: String,
        val writer: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime?,
    )
}