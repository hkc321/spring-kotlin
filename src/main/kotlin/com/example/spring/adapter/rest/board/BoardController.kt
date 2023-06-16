package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.BoardRequest
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.config.BaseController
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("board")
class BoardController(private val boardUseCase: BoardUseCase) : BaseController() {
    @GetMapping("all")
    fun readBoardList(
        @ModelAttribute boardReadBoardListRequest: BoardReadBoardListRequest
    ): ResponseEntity<Any> =
        ResponseEntity.ok(boardUseCase.readBoardList(boardReadBoardListRequest))


    @GetMapping("{boardId}")
    fun readBoard(@PathVariable("boardId") boardId: Int): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.readBoard(boardId))

    @PostMapping("")
    fun writeBoard(@RequestBody body: BoardRequest): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.writeBoard(body.toDomain()))

    @PutMapping("{boardId}")
    fun editBoard(@RequestBody body: BoardRequest, @PathVariable boardId: Int): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.editBoard(body.toDomain(), boardId))

    @DeleteMapping("{boardId}")
    fun deleteBoard(@PathVariable("boardId") boardId: Int) =
        boardUseCase.deleteBoard(boardId)
}