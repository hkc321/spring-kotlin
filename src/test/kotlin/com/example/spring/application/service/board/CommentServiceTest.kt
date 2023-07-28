package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.application.port.out.board.CommentKotlinJdslPort
import com.example.spring.application.port.out.board.CommentRedisPort
import com.example.spring.application.service.board.exception.CommentDataNotFoundException
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.exception.WriterNotMatchException
import com.example.spring.domain.board.Comment
import com.example.spring.domain.board.Post
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class CommentServiceTest : BehaviorSpec({
    val commentJpaPort = mockk<CommentJpaPort>()
    val commentKotlinJdslPort = mockk<CommentKotlinJdslPort>()
    val postUseCase = mockk<PostUseCase>()
    val commentRedisPort = mockk<CommentRedisPort>()

    val commentService = CommentService(commentJpaPort, commentKotlinJdslPort, postUseCase, commentRedisPort)

    given("Read comment") {
        val readCommend = mockk<CommentUseCase.Commend.ReadCommend>()
        val post = mockk<Post>()

        When("comment not exist") {

            every { postUseCase.readPost(PostUseCase.Commend.ReadCommend(1,1)) } returns post
            every { readCommend.boardId } returns 1
            every { readCommend.postId } returns 1
            every { readCommend.commentId } returns 1
            every { readCommend.reader } returns "test"
            every { commentJpaPort.readComment(any(), any(), any()) } returns null

            Then("it should throw CommentDataNotFoundException") {
                shouldThrowUnit<CommentDataNotFoundException> {
                    commentService.readComment(readCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "댓글이 존재하지 않습니다. [boardId: 1, postId: 1, commentId: 1]"
                }
            }
        }
    }

    given("Update comment") {
        val updateCommend = mockk<CommentUseCase.Commend.UpdateCommend>()
        val post = mockk<Post>()
        val comment = mockk<Comment>()

        When("comment not exist") {

            every { postUseCase.readPost(PostUseCase.Commend.ReadCommend(2,2)) } returns post
            every { updateCommend.boardId } returns 2
            every { updateCommend.postId } returns 2
            every { updateCommend.commentId } returns 2
            every { updateCommend.modifier } returns "test"
            every { commentJpaPort.readComment(any(), any(), any()) } returns null

            Then("it should throw CommentDataNotFoundException") {
                shouldThrowUnit<CommentDataNotFoundException> {
                    commentService.updateComment(updateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "댓글이 존재하지 않습니다. [boardId: 2, postId: 2, commentId: 2]"
                }
            }
        }

        When("writer not match") {

            every { postUseCase.readPost(PostUseCase.Commend.ReadCommend(2,2)) } returns post
            every { updateCommend.boardId } returns 2
            every { updateCommend.postId } returns 2
            every { updateCommend.commentId } returns 2
            every { updateCommend.content } returns "test"
            every { updateCommend.modifier } returns "test"
            every { commentJpaPort.readComment(any(), any(), any()) } returns comment
            every { comment.update("test", "test") } throws WriterNotMatchException()

            Then("it should throw WriterNotMatchException") {
                shouldThrowUnit<WriterNotMatchException> {
                    commentService.updateComment(updateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.INVALID_USER
                    this.message shouldBe "작성자만 수정이 가능합니다."
                }
            }
        }
    }

    given("Like comment") {
        val likeCommend = mockk<CommentUseCase.Commend.LikeCommend>()
        val post = mockk<Post>()

        When("comment not exist") {

            every { postUseCase.readPost(PostUseCase.Commend.ReadCommend(3,3)) } returns post
            every { likeCommend.boardId } returns 3
            every { likeCommend.postId } returns 3
            every { likeCommend.commentId } returns 3
            every { commentJpaPort.readComment(any(), any(), any()) } returns null

            Then("it should throw CommentDataNotFoundException") {
                shouldThrowUnit<CommentDataNotFoundException> {
                    commentService.likeComment(likeCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "댓글이 존재하지 않습니다. [boardId: 3, postId: 3, commentId: 3]"
                }
            }
        }
    }

    given("delete like comment") {
        val likeCommend = mockk<CommentUseCase.Commend.LikeCommend>()
        val post = mockk<Post>()

        When("comment not exist") {

            every { postUseCase.readPost(PostUseCase.Commend.ReadCommend(4,4)) } returns post
            every { likeCommend.boardId } returns 4
            every { likeCommend.postId } returns 4
            every { likeCommend.commentId } returns 4
            every { commentJpaPort.readComment(any(), any(), any()) } returns null

            Then("it should throw CommentDataNotFoundException") {
                shouldThrowUnit<CommentDataNotFoundException> {
                    commentService.deleteLikeComment(likeCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "댓글이 존재하지 않습니다. [boardId: 4, postId: 4, commentId: 4]"
                }
            }
        }
    }

})
