package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.config.NoDataException
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Board
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class BoardJpaAdapter(private val boardJpaRepository: BoardJpaRepository) : BoardJpaPort {
    val boardJpaMapper = BoardJpaMapper.INSTANCE

    override fun getAllBoard(): List<Board> {
        val entities = boardJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        if (entities.size < 1) {
            throw NoDataException(ErrorCode.DATA_NOT_FOUND)
        }
        return entities.map { boardJpaMapper.toBoard(it) }
    }

    override fun getDetail(boardId: Int): Board {
        val detail = boardJpaRepository.findByBoardId(boardId)
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
        return boardJpaMapper.toBoard(detail)
    }

    override fun write(board: Board): Board {
        return boardJpaMapper.toBoard(boardJpaRepository.save(boardJpaMapper.toEntity(board)))
    }

    @Transactional
    override fun edit(board: Board, boardId: Int): Board {
        val detail = boardJpaRepository.findByBoardId(boardId)
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
        detail.title = board.title
        detail.content = board.content
        detail.up = board.up
        detail.writer = board.writer
        detail.editedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        return boardJpaMapper.toBoard(detail)
    }

    override fun delete(boardId: Int) {
        boardJpaRepository.findByBoardId(boardId)
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
        boardJpaRepository.deleteById(boardId)
    }

}