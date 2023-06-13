package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.config.NoDataException
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class BoardJpaAdapter(private val boardJpaRepository: BoardJpaRepository) : BoardJpaPort {
    val boardJpaMapper = BoardJpaMapper.INSTANCE

    override fun loadAllBoard(pageable: Pageable): Page<Board> =
        boardJpaRepository.findAll(pageable).map {
            boardJpaMapper.toBoard(it)
        }

    override fun loadBoard(boardId: Int): Board {
        boardJpaRepository.findByBoardId(boardId)
            ?.let {
                return boardJpaMapper.toBoard(it)
            }
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }

    override fun saveBoard(board: Board): Board {
        return boardJpaMapper.toBoard(boardJpaRepository.save(boardJpaMapper.toEntity(board)))
    }

    @Transactional
    override fun editBoard(board: Board, boardId: Int): Board {
        boardJpaRepository.findByBoardId(boardId)
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
        boardJpaRepository.findByBoardId(boardId)
            ?.let {
                boardJpaRepository.deleteById(boardId)
            }
            ?: throw NoDataException(ErrorCode.DATA_NOT_FOUND)
    }

}