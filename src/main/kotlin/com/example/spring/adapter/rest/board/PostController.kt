package com.example.spring.adapter.rest.board

import com.example.spring.adapter.rest.board.dto.*
import com.example.spring.adapter.rest.board.mapper.PostRestMapper
import com.example.spring.adapter.rest.board.mapper.PostSingleReponseMapper
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.domain.board.Post
import jakarta.validation.constraints.Pattern
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
@RequestMapping("boards/{boardId}/posts")
class PostController(private val postUseCase: PostUseCase, private val postSingleReponseMapper: PostSingleReponseMapper) {
    private val postRestMapper = PostRestMapper.INSTANCE

    @PostMapping("")
    fun createPost(
        @PathVariable("boardId") boardId: Int,
        @RequestBody body: PostCreateRequest,
        principal: Principal
    ): ResponseEntity<PostSingleResponse> {
        val createdPost: Post = postUseCase.createPost(
            PostUseCase.Commend.CreateCommend(
                boardId = boardId,
                title = body.title,
                content = body.content,
                writer = principal.name
            )
        )
        val location = "/board/${createdPost.boardId}/posts/${createdPost.postId}"
        return ResponseEntity.created(URI.create(location)).body(
            postSingleReponseMapper.toPostSingleResponse(createdPost)
        )
    }

    @GetMapping()
    fun readPostPageList(
        @PathVariable("boardId") boardId: Int,
        @RequestParam("keyword") keyword: String? = null,
        @RequestParam("searchType")
        @Pattern(
            regexp = "\\b(?:title|content|writer)\\b",
            message = "[searchType]: title, content 혹은 writer만 허용됩니다."
        ) searchType: String? = null,
        @PageableDefault(
            page = 0,
            size = 20
        )
        @SortDefault.SortDefaults(
            SortDefault(sort = ["postId"], direction = Sort.Direction.DESC)
        )
        pageable: Pageable
    ): ResponseEntity<PostReadPageListResponse> =
        ResponseEntity.ok(
            postUseCase.readPostPageList(
                PostUseCase.Commend.ReadListCommend(
                    boardId = boardId,
                    keyword = keyword,
                    searchType = searchType,
                    pageable = pageable
                )
            ).map {
                postRestMapper.toPostCommonResponse(it)
            }.run {
                postRestMapper.toPostReadPageListResponse(this)
            }
        )

    @GetMapping("{postId}")
    fun readPost(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        principal: Principal
    ): ResponseEntity<PostSingleResponse> =
        ResponseEntity.ok(
            postSingleReponseMapper.toPostSingleResponse(
                postUseCase.readPost(
                    PostUseCase.Commend.ReadCommend(
                        boardId = boardId,
                        postId = postId,
                        reader = principal.name
                    )
                )
            )
        )

    @PatchMapping("{postId}")
    fun updatePost(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        @RequestBody body: PostUpdateRequest,
        principal: Principal
    ): ResponseEntity<PostSingleResponse> =
        ResponseEntity.ok(
            postSingleReponseMapper.toPostSingleResponse(
                postUseCase.updatePost(
                    PostUseCase.Commend.UpdateCommend(
                        boardId = boardId,
                        postId = postId,
                        title = body.title,
                        content = body.content,
                        modifier = principal.name
                    )
                )
            )
        )

    @PatchMapping("{postId}/like")
    fun updatePostLike(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        principal: Principal
    ): ResponseEntity<PostSingleResponse> =
        ResponseEntity.ok(
            postSingleReponseMapper.toPostSingleResponse(
                postUseCase.likePost(
                    PostUseCase.Commend.LikeCommend(
                        boardId = boardId,
                        postId = postId,
                        email = principal.name
                    )
                )
            )
        )

    @DeleteMapping("{postId}/like")
    fun deletePostLike(
        @PathVariable("boardId") boardId: Int,
        @PathVariable("postId") postId: Int,
        principal: Principal
    ): ResponseEntity<PostSingleResponse> =
        ResponseEntity.ok(
            postSingleReponseMapper.toPostSingleResponse(
                postUseCase.deleteLikePost(
                    PostUseCase.Commend.LikeCommend(
                        boardId = boardId,
                        postId = postId,
                        email = principal.name
                    )
                )
            )
        )

    @DeleteMapping("{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePost(@PathVariable("boardId") boardId: Int, @PathVariable("postId") postId: Int, principal: Principal) =
        postUseCase.deletePost(
            PostUseCase.Commend.DeleteCommend(
                boardId = boardId,
                postId = postId,
                modifier = principal.name
            )
        )
}