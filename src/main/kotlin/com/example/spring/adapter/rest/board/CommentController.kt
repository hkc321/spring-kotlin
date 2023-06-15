package com.example.spring.adapter.rest.board

import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.config.BaseController
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("comment")
class CommentController(private val commentUseCase: CommentUseCase) : BaseController() {
    @GetMapping("{boardId}")
    fun readBoardCommentList(
        @PathVariable("boardId") boardId: Int,
        @PageableDefault(
            page = 0,
            size = 10
        )
        @SortDefault.SortDefaults(
            SortDefault(sort = ["parentCommentId"], direction = Sort.Direction.DESC)
        )
        pageable: Pageable
    ): ResponseEntity<Any> =
        ResponseEntity.ok(commentUseCase.readBoardCommentTopLevelList(boardId, pageable))
}