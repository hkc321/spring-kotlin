package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.CommentReadChildCommentResponse
import com.example.spring.adapter.rest.board.dto.CommentRequest
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.config.BaseController
import com.example.spring.domain.board.Comment
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("comment")
class CommentController(private val commentUseCase: CommentUseCase) : BaseController() {
    @PostMapping("")
    fun createComment(@RequestBody body: CommentRequest): ResponseEntity<Comment> =
        ResponseEntity.ok(commentUseCase.createComment(body.toDomain()))

    @GetMapping("{commentId}")
    fun readComment(@PathVariable("commentId") commentId: Int): ResponseEntity<Comment> =
        ResponseEntity.ok(commentUseCase.readComment(commentId))

    @GetMapping("{commentId}/childComment")
    fun readChildComment(
        @PathVariable("commentId") commentId: Int,
        @PageableDefault(
            page = 0,
            size = 20
        )
        @SortDefault.SortDefaults(
            SortDefault(sort = ["commentId"], direction = Sort.Direction.ASC)
        )
        pageable: Pageable
    ): ResponseEntity<CommentReadChildCommentResponse> =
        ResponseEntity.ok(commentUseCase.readChildComment(commentId, pageable))

    @PatchMapping("{commentId}")
    fun updateComment(
        @PathVariable("commentId") commentId: Int,
        @RequestBody body: CommentRequest
    ): ResponseEntity<Comment> =
        ResponseEntity.ok(commentUseCase.updateComment(commentId, body.toDomain()))
}