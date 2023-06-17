package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.mapper.CommentJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.adapter.jpa.board.repository.CommentJpaRepository
import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.BoardReadTopLevelCommentOnBoardResponse
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.config.NoDataException
import com.example.spring.config.common.Pagination
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Board
import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.selectQuery
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class BoardJpaAdapter(
    private val boardJpaRepository: BoardJpaRepository,
    private val entityManager: EntityManager,
    private val commentJpaRepository: CommentJpaRepository
) :
    BoardJpaPort {
    val boardJpaMapper = BoardJpaMapper.INSTANCE
    val commentJpaMapper = CommentJpaMapper.INSTANCE

    override fun loadAllBoard(boardReadBoardListRequest: BoardReadBoardListRequest): HashMap<String, Any> {
        val queryFactory: QueryFactory = QueryFactoryImpl(
            criteriaQueryCreator = CriteriaQueryCreatorImpl(entityManager),
            subqueryCreator = SubqueryCreatorImpl()
        )

        // 조건에 맞는 게시물 총 갯수
        val totalCount = queryFactory.selectQuery<Long> {
            val count = count(column(BoardJpaEntity::boardId))
            select(count)
            from(entity(BoardJpaEntity::class))
            where(
                boardReadBoardListRequest.keyword?.let {
                    when (boardReadBoardListRequest.searchType) {
                        "content" -> column(BoardJpaEntity::content).like(it)
                        "title" -> column(BoardJpaEntity::title).like(it)
                        "writer" -> column(BoardJpaEntity::writer).like(it)
                        else -> null
                    }
                }
            )
        }.singleResult.toInt()

        // 게시물 가져오기
        val pagination = Pagination(totalCount, boardReadBoardListRequest)
        val pageList: List<Board> = queryFactory.listQuery<BoardJpaEntity> {
            select(entity(BoardJpaEntity::class))
            from(entity(BoardJpaEntity::class))
            where(
                boardReadBoardListRequest.keyword?.let {
                    when (boardReadBoardListRequest.searchType) {
                        "content" -> column(BoardJpaEntity::content).like(it)
                        "title" -> column(BoardJpaEntity::title).like(it)
                        "writer" -> column(BoardJpaEntity::writer).like(it)
                        else -> null
                    }
                }
            )
            if (boardReadBoardListRequest.orderBy == "boardId") {
                orderBy(
                    ExpressionOrderSpec(column(BoardJpaEntity::boardId), false)
                )
            } else if (boardReadBoardListRequest.orderBy == "up") {
                orderBy(
                    ExpressionOrderSpec(column(BoardJpaEntity::up), false)
                )
            }
            limit(pagination.limitStart, boardReadBoardListRequest.recordSize)
        }.map { boardJpaMapper.toBoard(it) }

        return hashMapOf(
            "paginationInfo" to pagination,
            "pageList" to pageList
        )
    }

    override fun loadBoard(boardId: Int): Board {
        boardJpaRepository.findByIdOrNull(boardId)
            ?.let {
                return boardJpaMapper.toBoard(it)
            }
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }

    override fun saveBoard(board: Board): Board =
        boardJpaMapper.toBoard(boardJpaRepository.save(boardJpaMapper.toEntity(board)))

    @Transactional
    override fun editBoard(board: Board, boardId: Int): Board {
        boardJpaRepository.findByIdOrNull(boardId)
            ?.let {
                it.title = board.title
                it.content = board.content
                it.up = board.up
                it.writer = board.writer
                it.editedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                return boardJpaMapper.toBoard(it)
            }
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }

    override fun deleteBoard(boardId: Int) {
        boardJpaRepository.findByIdOrNull(boardId)
            ?.let {
                boardJpaRepository.deleteById(boardId)
            }
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }

    override fun readTopLevelCommentOnBoard(boardId: Int, pageable: Pageable): BoardReadTopLevelCommentOnBoardResponse {
        commentJpaRepository.findPageByBoardIdAndLevel(boardId, pageable).map {
            commentJpaMapper.toComment(it).apply {
                this.childCommentCount =
                    commentJpaRepository.countByParentCommentIdAndCommentIdIsNot(it.parentCommentId, it.commentId)
            }
        }.apply {
            return BoardReadTopLevelCommentOnBoardResponse(
                isEmpty,
                isLast,
                totalElements.toInt(),
                pageable.pageNumber,
                content
            )
        }
    }

}