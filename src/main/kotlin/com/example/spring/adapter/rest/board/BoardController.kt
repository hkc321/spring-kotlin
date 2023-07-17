package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.BoardCommonResponse
import com.example.spring.adapter.rest.board.dto.BoardCreateRequest
import com.example.spring.adapter.rest.board.dto.BoardReadPageListResponse
import com.example.spring.adapter.rest.board.dto.BoardUpdateRequest
import com.example.spring.adapter.rest.board.mapper.BoardRestMapper
import com.example.spring.application.port.`in`.board.BoardUseCase
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.security.Principal

@Validated
@RestController
@RequestMapping("boards")
class BoardController(private val boardUseCase: BoardUseCase) {
    val boardRestMapper = BoardRestMapper.INSTANCE

    @PostMapping("")
    fun createBoard(@RequestBody body: BoardCreateRequest, principal: Principal): ResponseEntity<BoardCommonResponse> {
        val createdBoard = boardUseCase.createBoard(
            BoardUseCase.Commend.CreateCommend(
                name = body.name,
                description = body.description,
                writer = principal.name
            )
        )
        val location = "/board/${createdBoard.boardId}"
        return ResponseEntity.created(URI.create(location)).body(
            boardRestMapper.toBoardCommonResponse(createdBoard)
        )
    }

    @GetMapping("")
    fun readBoardPageList(
        @RequestParam("keyword") keyword: String? = null,
        @RequestParam("searchType")
        @Pattern(
            regexp = "\\b(?:title|content|writer)\\b",
            message = "[searchType]: content, description 혹은 writer만 허용됩니다."
        ) searchType: String? = null,
        @RequestParam("page")
        @Min(value = 1, message = "[page]: 1 이상이여야 합니다.") page: Int,
        @RequestParam("size")
        @Max(value = 50, message = "[size]: 50 이하여야 합니다.")
        @Min(value = 1, message = "[size]: 1 이상이여야 합니다.") size: Int
    ): ResponseEntity<BoardReadPageListResponse> =
        ResponseEntity.ok(
            boardUseCase.readBoardPageList(
                BoardUseCase.Commend.ReadListCommend(
                    keyword = keyword,
                    searchType = searchType,
                    pageable = PageRequest.of(page - 1, size, Sort.by("boardId").descending())
                )
            ).map {
                boardRestMapper.toBoardCommonResponse(it)
            }.run {
                boardRestMapper.toBoardReadPageListResponse(this)
            }
        )

    @GetMapping("{boardId}")
    fun readBoard(@PathVariable("boardId") boardId: Int): ResponseEntity<BoardCommonResponse> =
        ResponseEntity.ok(
            boardRestMapper.toBoardCommonResponse(
                boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(boardId = boardId))
            )
        )

    @PatchMapping("{boardId}")
    fun updateBoard(
        @RequestBody body: BoardUpdateRequest,
        @PathVariable("boardId") boardId: Int,
        principal: Principal
    ): ResponseEntity<BoardCommonResponse> =
        ResponseEntity.ok(
            boardRestMapper.toBoardCommonResponse(
                boardUseCase.updateBoard(
                    BoardUseCase.Commend.UpdateCommend(
                        boardId = boardId,
                        name = body.name,
                        description = body.description,
                        modifier = principal.name
                    )
                )
            )
        )

    @DeleteMapping("{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBoard(@PathVariable("boardId") boardId: Int) =
        boardUseCase.deleteBoard(BoardUseCase.Commend.DeleteCommend(boardId = boardId))
}