package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BoardService(private val boardJpaPort: BoardJpaPort) : BoardUseCase {
    override fun all(): ResponseEntity<Any> = ResponseEntity.ok(boardJpaPort.getAllBoard())

    override fun detail(boardId: Int): ResponseEntity<Any> = ResponseEntity.ok(boardJpaPort.getDetail(boardId))

    override fun write(): ResponseEntity<Any> {
        TODO("Not yet implemented")
    }

    override fun edit() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }
}