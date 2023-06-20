package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.BoardReadBoardListRequest
import com.example.spring.adapter.rest.board.dto.BoardRequest
import com.example.spring.adapter.rest.board.dto.BoardReadTopLevelCommentOnBoardResponse
import com.example.spring.adapter.rest.board.dto.BoardUpdateBoardRequest
import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.config.BaseController
import com.example.spring.domain.board.Board
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("board")
class BoardController(private val boardUseCase: BoardUseCase) : BaseController() {
    @GetMapping("")
    fun readBoardList(
        @ModelAttribute boardReadBoardListRequest: BoardReadBoardListRequest
    ): ResponseEntity<Any> =
        ResponseEntity.ok(boardUseCase.readBoardList(boardReadBoardListRequest))


    @GetMapping("{boardId}")
    fun readBoard(@PathVariable("boardId") boardId: Int): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.readBoard(boardId))

    @PostMapping("")
    fun writeBoard(@RequestBody body: BoardRequest): ResponseEntity<Board> {
        val createdBoard: Board = boardUseCase.writeBoard(body.toDomain())
        val location = "/board/${createdBoard.boardId}"
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", location).body(createdBoard)
    }

    @PatchMapping("{boardId}")
    fun updateBoard(@RequestBody body: BoardUpdateBoardRequest, @PathVariable boardId: Int): ResponseEntity<Board> =
        ResponseEntity.ok(boardUseCase.updateBoard(body.toDomain(), boardId))

    @DeleteMapping("{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBoard(@PathVariable("boardId") boardId: Int) = boardUseCase.deleteBoard(boardId)

    @GetMapping("{boardId}/comment")
    fun readTopLevelCommentOnBoard(
        @PathVariable("boardId") boardId: Int,
        @PageableDefault(
            page = 0,
            size = 10
        )
        @SortDefault.SortDefaults(
            SortDefault(sort = ["parentCommentId"], direction = Sort.Direction.DESC)
        )
        pageable: Pageable
    ): ResponseEntity<BoardReadTopLevelCommentOnBoardResponse> =
        ResponseEntity.ok(boardUseCase.readTopLevelCommentOnBoard(boardId, pageable))
}