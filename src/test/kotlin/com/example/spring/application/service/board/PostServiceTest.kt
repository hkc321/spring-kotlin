package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.application.port.out.board.PostRedisPort
import com.example.spring.application.service.board.exception.PostLikeException
import com.example.spring.config.code.ErrorCode
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class PostServiceTest : BehaviorSpec({
    val postJpaPort = mockk<PostJpaPort>()
    val postKotlinJdslPort = mockk<PostKotlinJdslPort>()
    val boardUseCase = mockk<BoardUseCase>()
    val postRedisPort = mockk<PostRedisPort>()
    val postService = PostService(postJpaPort, postKotlinJdslPort, boardUseCase, postRedisPort)

    Given("a like post command") {
        val likeCommand = PostUseCase.Commend.LikeCommend(
            boardId = 2,
            postId = 2,
            email = "user@example.com"
        )

        When("likePost is called 1000 times") {
            val initialLikeCount = 0
            val expectedUpdatedLikeCount = 1000 // The expected updated like count after 1000 likes

            var currentLikeCount = initialLikeCount
            every { postRedisPort.createPostLike(any(), any(), any()) } answers {
                currentLikeCount++
                currentLikeCount
            }

            val post = Post(4, 2, "test", "test", "test")
            post.updateLike(initialLikeCount)
            every { postKotlinJdslPort.readPost(any(), any()) } returns post

            val board = Board(2, "test", "test", "test", "test")
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(likeCommand.boardId)) } returns board

            every { postJpaPort.updatePost(any()) } returns post

            repeat(1000) {
                postService.likePost(likeCommand)
            }

            Then("the post's like count is increased by 1 after 1000 likes") {
                verify(exactly = 1000) { postRedisPort.createPostLike(any(), any(), any()) }
                verify(exactly = 1000) { postKotlinJdslPort.readPost(any(), any()) }
                verify(exactly = 1000) { postJpaPort.updatePost(any()) }

                post.like shouldBe expectedUpdatedLikeCount
            }
        }

        When("same member try to like post twice") {
            val expectedException = PostLikeException(
                likeCommand.boardId,
                likeCommand.postId,
                ErrorCode.ALREADY_EXIST,
                "이미 좋아요를 클릭한 게시글입니다. [boardId: $likeCommand.boardId, postId: $likeCommand.postId]"
            )
            every {
                postRedisPort.createPostLike(
                    likeCommand.boardId,
                    likeCommand.postId,
                    likeCommand.email
                )
            } throws expectedException

            Then("an exception should be thrown") {
                val exception = shouldThrow<PostLikeException> {
                    postService.likePost(likeCommand)
                }
                exception.code shouldBe ErrorCode.ALREADY_EXIST

                verify(exactly = 1) {
                    postRedisPort.createPostLike(likeCommand.boardId, likeCommand.postId, likeCommand.email)
                }
                verify(exactly = 0) {
                    postKotlinJdslPort.readPost(any(), any())
                    postJpaPort.updatePost(any())
                }
            }
        }

        When("correct request") {
            val likeCount = 1
            every { postRedisPort.createPostLike(any(), any(), any()) } returns likeCount

            val post = Post(4, 2, "test", "test", "test")
            post.updateLike(likeCount)
            every { postKotlinJdslPort.readPost(any(), any()) } returns post

            val board = Board(2, "test", "test", "test", "test")
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(likeCommand.boardId)) } returns board

            every { postJpaPort.updatePost(any()) } returns post

            postService.likePost(likeCommand)

            Then("the post's like count is same likeCount") {
                verify(exactly = 1) { postRedisPort.createPostLike(any(), any(), any()) }
                verify(exactly = 1) { postKotlinJdslPort.readPost(any(), any()) }
                verify(exactly = 1) { postJpaPort.updatePost(any()) }

                post.like shouldBe likeCount
            }
        }
    }

    // Add more test cases for other methods of PostService

    afterContainer {
        clearAllMocks()
    }

    afterSpec {
        clearAllMocks()
    }

})
