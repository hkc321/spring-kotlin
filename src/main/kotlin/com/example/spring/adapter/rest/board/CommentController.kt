package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.*
import com.example.spring.adapter.rest.board.mapper.CommentRestMapper
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.config.BaseController
import com.example.spring.domain.board.Comment
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
@RequestMapping("boards/{boardId}/posts/{postId}/comments")
class CommentController(private val commentUseCase: CommentUseCase) : BaseController() {
    private val commentRestMapper = CommentRestMapper.INSTANCE

    @PostMapping("")
    fun createComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @Valid @RequestBody body: CommentCreateRequest,
        principal: Principal
    ): ResponseEntity<CommentCommonResponse> {
        val createdComment: Comment = commentUseCase.createComment(
            CommentUseCase.Commend.CreateCommend(
                boardId = boardId,
                postId = postId,
                parentCommentId = body.parentCommentId,
                level = body.level,
                content = body.content,
                writer = principal.name
            )
        )
        val location =
            "/boards/${createdComment.boardId}/posts/${createdComment.postId}/comments/${createdComment.commentId}"
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", location)
            .body(
                commentRestMapper.toCommentCommonResponse(createdComment)
            )
    }

    @GetMapping("")
    fun readTopLevelComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @RequestParam("size", required = true) @Max(value = 50, message = "[size]: 50 이하여야 합니다.") size: Int,
        @RequestParam("cursor", required = false) cursor: Int?,
        @RequestParam("orderBy", required = true)
        @Pattern(
            regexp = "\\b(?:up|recent)\\b",
            message = "[orderBy]: up 혹은 recent만 허용됩니다."
        ) orderBy: String
    ): ResponseEntity<CommentTopLevelResponse> =
        ResponseEntity.ok(
            commentUseCase.readTopLevelComment(
                CommentUseCase.Commend.ReadTopLevelCommend(
                    boardId,
                    postId,
                    size,
                    cursor,
                    orderBy
                )
            ).let {
                it.first.map { comment ->
                    commentRestMapper.toCommentCommonResponse(comment)
                }.let { changed ->
                    CommentTopLevelResponse(changed, it.second)
                }
            }
        )

    @GetMapping("{commentId}")
    fun readComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int
    ): ResponseEntity<CommentCommonResponse> =
        ResponseEntity.ok(
            commentRestMapper.toCommentCommonResponse(
                commentUseCase.readComment(
                    CommentUseCase.Commend.ReadCommend(
                        boardId = boardId,
                        postId = postId,
                        commentId = commentId
                    )
                )
            )
        )


    @GetMapping("{commentId}/childComment")
    fun readChildComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        @RequestParam("size", required = true) @Max(value = 50, message = "[size]: 50 이하여야 합니다.") size: Int,
        @RequestParam("cursor", required = false) cursor: Int?
    ): ResponseEntity<CommentChildResponse> =
        ResponseEntity.ok(
            commentUseCase.readChildComment(
                CommentUseCase.Commend.ReadChildCommend(
                    boardId,
                    postId,
                    commentId,
                    size,
                    cursor
                )
            ).let {
                it.first.map { comment ->
                    commentRestMapper.toCommentCommonResponse(comment)
                }.let { changed ->
                    CommentChildResponse(changed, it.second)
                }

            }
        )

    @PatchMapping("{commentId}")
    fun updateComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        @RequestBody body: CommentUpdateRequest
    ): ResponseEntity<CommentCommonResponse> =
        ResponseEntity.ok(
            commentRestMapper.toCommentCommonResponse(
                commentUseCase.updateComment(
                    CommentUseCase.Commend.UpdateCommend(
                        boardId,
                        postId,
                        commentId,
                        body.content
                    )
                )
            )
        )

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int
    ) = commentUseCase.deleteComment(CommentUseCase.Commend.DeleteCommend(boardId, postId, commentId))
}