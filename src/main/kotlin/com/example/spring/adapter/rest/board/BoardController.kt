package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.BoardRequest
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.config.BaseController
import com.example.spring.domain.board.Board
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("board")
class BoardController(private val boardUseCase: BoardUseCase) : BaseController() {
    @GetMapping("all")
    fun all(): ResponseEntity<Any> =
        ResponseEntity.ok(boardUseCase.all())

    @GetMapping("detail/{boardId}")
    fun detail(@PathVariable("boardId") boardId: Int): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.detail(boardId))

    @PostMapping("write")
    fun write(@RequestBody body: BoardRequest): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.write(body.toDomain()))

    @PutMapping("edit/{boardId}")
    fun edit(@RequestBody body: BoardRequest, @PathVariable boardId: Int): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.edit(body.toDomain(), boardId))

    @DeleteMapping("delete/{boardId}")
    fun delete(@PathVariable("boardId") boardId: Int) =
        boardUseCase.delete(boardId)
}