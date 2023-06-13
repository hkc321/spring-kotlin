package com.example.spring.application.service.board

import com.example.spring.adapter.jpa.board.BoardJpaAdapter
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.config.BaseResponseException
import com.example.spring.config.dto.ErrorCode
import com.example.spring.domain.board.Board
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BoardService(private val boardJpaPort: BoardJpaPort) : BoardUseCase {
    override fun all(): ResponseEntity<Any> {
        try {
            return ResponseEntity.ok(boardJpaPort.getAllBoard())
        } catch (ex: BoardJpaAdapter.BoardNoDataException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    BaseResponseException(
                        ex.code,
                        ex.message
                    )
                )
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    BaseResponseException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        ex.message.toString()
                    )
                )
        }
    }

    override fun detail(boardId: Int): ResponseEntity<Any> {
        try {
            return ResponseEntity.ok(boardJpaPort.getDetail(boardId))
        } catch (ex: BoardJpaAdapter.BoardNoDataException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    BaseResponseException(
                        ex.code,
                        ex.message
                    )
                )
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    BaseResponseException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        ex.message.toString()
                    )
                )
        }


    }

    override fun write(board: Board): ResponseEntity<Any> {
        try {
            return ResponseEntity.ok(boardJpaPort.write(board))
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    BaseResponseException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        ex.message.toString()
                    )
                )
        }
    }

    override fun edit(board: Board, boardId: Int): ResponseEntity<Any> {
        try {
            return ResponseEntity.ok(boardJpaPort.edit(board, boardId))
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    BaseResponseException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        ex.message.toString()
                    )
                )
        }
    }

    override fun delete() {
        TODO("Not yet implemented")
    }
}