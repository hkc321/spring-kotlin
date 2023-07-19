package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.*
import com.example.spring.adapter.rest.board.mapper.CommentSingleResponseMapper
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.domain.board.Comment
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
@RequestMapping("boards/{boardId}/posts/{postId}/comments")
class CommentController(
    private val commentUseCase: CommentUseCase,
    private val commentSingleResponseMapper: CommentSingleResponseMapper
) {

    @PostMapping("")
    fun createComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @Valid @RequestBody body: CommentCreateRequest,
        principal: Principal
    ): ResponseEntity<CommentSingleResponse> {
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
                commentSingleResponseMapper.toCommentSingleResponse(createdComment)
            )
    }

    @GetMapping("")
    fun readTopLevelComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @RequestParam("size", required = true)
        @Max(value = 50, message = "[size]: 50 이하여야 합니다.")
        @Min(value = 1, message = "[size]: 1 이상이여야 합니다.") size: Int,
        @RequestParam("cursor", required = false) cursor: Int?,
        @RequestParam("orderBy", required = true)
        @Pattern(
            regexp = "\\b(?:like|recent)\\b",
            message = "[orderBy]: like 혹은 recent만 허용됩니다."
        ) orderBy: String,
        principal: Principal
    ): ResponseEntity<CommentTopLevelResponse> =
        ResponseEntity.ok(
            commentUseCase.readTopLevelComment(
                CommentUseCase.Commend.ReadTopLevelCommend(
                    boardId,
                    postId,
                    size,
                    cursor,
                    orderBy,
                    principal.name
                )
            ).let {
                it.first.map { comment ->
                    commentSingleResponseMapper.toCommentSingleResponse(comment)
                }.let { changed ->
                    CommentTopLevelResponse(changed, it.second)
                }
            }
        )

    @GetMapping("{commentId}")
    fun readComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        principal: Principal
    ): ResponseEntity<CommentSingleResponse> =
        ResponseEntity.ok(
            commentSingleResponseMapper.toCommentSingleResponse(
                commentUseCase.readComment(
                    CommentUseCase.Commend.ReadCommend(
                        boardId = boardId,
                        postId = postId,
                        commentId = commentId,
                        principal.name
                    )
                )
            )
        )


    @GetMapping("{commentId}/childComment")
    fun readChildComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        @RequestParam("size", required = true)
        @Max(value = 50, message = "[size]: 50 이하여야 합니다.")
        @Min(value = 1, message = "[size]: 1 이상이여야 합니다.") size: Int,
        @RequestParam("cursor", required = false) cursor: Int?,
        principal: Principal
    ): ResponseEntity<CommentChildResponse> =
        ResponseEntity.ok(
            commentUseCase.readChildComment(
                CommentUseCase.Commend.ReadChildCommend(
                    boardId,
                    postId,
                    commentId,
                    size,
                    cursor,
                    principal.name
                )
            ).let {
                it.first.map { comment ->
                    commentSingleResponseMapper.toCommentSingleResponse(comment)
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
        @RequestBody body: CommentUpdateRequest,
        principal: Principal
    ): ResponseEntity<CommentSingleResponse> =
        ResponseEntity.ok(
            commentSingleResponseMapper.toCommentSingleResponse(
                commentUseCase.updateComment(
                    CommentUseCase.Commend.UpdateCommend(
                        boardId,
                        postId,
                        commentId,
                        body.content,
                        principal.name
                    )
                )
            )
        )

    @PatchMapping("{commentId}/like")
    fun updateCommentLike(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        principal: Principal
    ): ResponseEntity<CommentSingleResponse> =
        ResponseEntity.ok(
            commentSingleResponseMapper.toCommentSingleResponse(
                commentUseCase.likeComment(
                    CommentUseCase.Commend.LikeCommend(
                        boardId, postId, commentId, principal.name
                    )
                )
            )
        )

    @PatchMapping("{commentId}/unlike")
    fun deleteCommentLike(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        principal: Principal
    ): ResponseEntity<CommentSingleResponse> =
        ResponseEntity.ok(
            commentSingleResponseMapper.toCommentSingleResponse(
                commentUseCase.deleteLikeComment(
                    CommentUseCase.Commend.LikeCommend(
                        boardId, postId, commentId, principal.name
                    )
                )
            )
        )

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @PathVariable("commentId") commentId: Int,
        principal: Principal
    ) = commentUseCase.deleteComment(
        CommentUseCase.Commend.DeleteCommend(
            boardId,
            postId,
            commentId,
            principal.name
        )
    )
}