package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.BoardRequest
import com.example.spring.adapter.rest.member.dto.MemberRequest
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.config.BaseController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("board")
class BoardController(private val boardUseCase: BoardUseCase) : BaseController() {
    @GetMapping("all")
    fun all(): ResponseEntity<Any> = boardUseCase.all()

    @GetMapping("detail/{boardId}")
    fun detail(@PathVariable("boardId") boardId: Int): ResponseEntity<Any> = boardUseCase.detail(boardId)


    @PostMapping("write")
    fun write(@RequestBody body: BoardRequest): ResponseEntity<Any> = boardUseCase.write(body.toDomain())

    @PutMapping("edit/{boardId}")
    fun edit(@RequestBody body: BoardRequest, @PathVariable boardId: String): Boolean {
        return true
    }

    @DeleteMapping("delete/{boardId}")
    fun delete(@PathVariable("boardId") boardId: Int): Boolean {
        return true
    }
}