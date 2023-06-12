package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Board
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.lang.RuntimeException

@Repository
class BoardJpaAdapter(private val boardJpaRepository: BoardJpaRepository) : BoardJpaPort {
    val boardJpaMapper = BoardJpaMapper.INSTANCE

    override fun getAllBoard(): List<Board> {
        val entities = boardJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        if (entities.size < 1) {
            throw BoardNoDataException(ErrorCode.DATA_NOT_FOUND, "데이터가 존재하지 않습니다")
        }
        return entities.map { boardJpaMapper.toBoard(it) }
    }

    override fun getDetail(boardId: Int): Board {
        val detail = boardJpaRepository.findByBoardId(boardId) ?: throw BoardNoDataException(
            ErrorCode.DATA_NOT_FOUND,
            "데이터가 존재하지 않습니다"
        )
        return boardJpaMapper.toBoard(detail)
    }

    override fun write(board: Board): Board {
        return boardJpaMapper.toBoard(boardJpaRepository.save(boardJpaMapper.toEntity(board)))
    }

    data class BoardNoDataException(
        var code: ErrorCode,
        override var message: String
    ) : RuntimeException(message, null)
}