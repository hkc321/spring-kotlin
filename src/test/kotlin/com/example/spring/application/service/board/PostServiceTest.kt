package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.application.port.out.board.PostRedisPort
import com.example.spring.application.service.board.exception.PostDataNotFoundException
import com.example.spring.application.service.board.exception.PostLikeException
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.WriterNotMatchException
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
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

    Given("Read post") {
        val postReadCommend = mockk<PostUseCase.Commend.ReadCommend>()
        val board = mockk<Board>()

        When("post is not exist") {
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(1)) } returns board
            every { postReadCommend.boardId } returns 1
            every { postReadCommend.postId } returns 1
            every { postKotlinJdslPort.readPost(board, 1) } returns null

            Then("it should throw PostDataNotFoundException") {
                shouldThrowUnit<PostDataNotFoundException> {
                    postService.readPost(postReadCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "게시글이 존재하지 않습니다. [boardId: 1, postId: 1]"
                }
            }
        }
    }

    Given("Update post") {
        val postUpdateCommend = mockk<PostUseCase.Commend.UpdateCommend>()
        val board = mockk<Board>()
        val post = mockk<Post>()

        When("post is not exist") {
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(2)) } returns board
            every { postUpdateCommend.boardId } returns 2
            every { postUpdateCommend.postId } returns 2
            every { postKotlinJdslPort.readPost(board, 2) } returns null

            Then("it should throw PostDataNotFoundException") {
                shouldThrowUnit<PostDataNotFoundException> {
                    postService.updatePost(postUpdateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "게시글이 존재하지 않습니다. [boardId: 2, postId: 2]"
                }
            }
        }

        When("writer not match") {
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(2)) } returns board
            every { postUpdateCommend.boardId } returns 2
            every { postUpdateCommend.postId } returns 2
            every { postKotlinJdslPort.readPost(board, 2) } returns post
            every { postUpdateCommend.title } returns "test"
            every { postUpdateCommend.content } returns "test"
            every { postUpdateCommend.modifier } returns "test"
            every { post.update("test", "test", "test") } throws WriterNotMatchException()


            Then("it should throw WriterNotMatchException") {
                shouldThrowUnit<WriterNotMatchException> {
                    postService.updatePost(postUpdateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.INVALID_USER
                    this.message shouldBe "작성자만 수정이 가능합니다."
                }
            }
        }
    }

    Given("Delete post") {
        val postDeleteCommend = mockk<PostUseCase.Commend.DeleteCommend>()
        val board = mockk<Board>()
        val post = mockk<Post>()

        When("post is not exist") {
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(3)) } returns board
            every { postDeleteCommend.boardId } returns 3
            every { postDeleteCommend.postId } returns 3
            every { postKotlinJdslPort.readPost(board, 3) } returns null

            Then("it should throw PostDataNotFoundException") {
                shouldThrowUnit<PostDataNotFoundException> {
                    postService.deletePost(postDeleteCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "게시글이 존재하지 않습니다. [boardId: 3, postId: 3]"
                }
            }
        }

        When("writer not match") {
            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(3)) } returns board
            every { postDeleteCommend.boardId } returns 3
            every { postDeleteCommend.postId } returns 3
            every { postKotlinJdslPort.readPost(board, 3) } returns post
            every { postDeleteCommend.modifier } returns "test"
            every { post.checkWriter("test") } throws WriterNotMatchException()

            Then("it should throw WriterNotMatchException") {
                shouldThrowUnit<WriterNotMatchException> {
                    postService.deletePost(postDeleteCommend)
                }.apply {
                    this.code shouldBe ErrorCode.INVALID_USER
                    this.message shouldBe "작성자만 수정이 가능합니다."
                }
            }
        }
    }

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
            val board = mockk<Board>()
            val post = mockk<Post>()
            val expectedException = PostLikeException(
                likeCommand.boardId,
                likeCommand.postId,
                ErrorCode.ALREADY_EXIST,
                "이미 좋아요를 클릭한 게시글입니다. [boardId: $likeCommand.boardId, postId: $likeCommand.postId]"
            )

            every { boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(likeCommand.boardId)) } returns board
            every { postKotlinJdslPort.readPost(board,likeCommand.postId ) } returns post
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

                verify(exactly = 0) {
                    post.updateIsLiked(any())
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
