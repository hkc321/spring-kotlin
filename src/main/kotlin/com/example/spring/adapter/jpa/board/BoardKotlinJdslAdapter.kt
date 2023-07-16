package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.BoardJpaMapper
import com.example.spring.adapter.jpa.board.repository.BoardKotlinJdslRepository
import com.example.spring.application.port.out.board.BoardKotlinJdslPort
import com.example.spring.application.service.board.exception.BoardDataNotFoundException
import com.example.spring.domain.board.Board
import jakarta.persistence.NoResultException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class BoardKotlinJdslAdapter(
    private val boardKotlinJdslRepository: BoardKotlinJdslRepository,
    private val boardJpaMapper: BoardJpaMapper
) : BoardKotlinJdslPort {
    override fun readBoard(boardId: Int): Board {
        try {
            return boardKotlinJdslRepository.readBoard(boardId).let {
                boardJpaMapper.toBoard(it)
            }
        } catch (ex: NoResultException) {
            throw BoardDataNotFoundException(boardId = boardId)
        }
    }


    override fun readBoardPageList(keyword: String?, searchType: String?, pageable: Pageable): Page<Board> =
        boardKotlinJdslRepository.readBoardPageList(keyword, searchType, pageable).map {
            boardJpaMapper.toBoard(it)
        }
}