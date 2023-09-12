package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.CommentUseCase
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
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class CommentServiceTest : BehaviorSpec({
    val commentJpaPort = mockk<CommentJpaPort>()
    val commentKotlinJdslPort = mockk<CommentKotlinJdslPort>()
    val commentRedisPort = mockk<CommentRedisPort>()

    val commentService = CommentService(commentJpaPort, commentKotlinJdslPort, commentRedisPort)

    given("Read comment") {
        val readCommend = mockk<CommentUseCase.Commend.ReadCommend>()
        val post = mockk<Post>()

        When("comment not exist") {

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

    given("delete comment") {
        val deleteCommend = mockk<CommentUseCase.Commend.DeleteCommend>()
        val comment = mockk<Comment>()

        When("comment not exist") {
            every { deleteCommend.boardId } returns 0
            every { deleteCommend.postId } returns 0
            every { deleteCommend.commentId } returns 0
            every { deleteCommend.modifier } returns "test"
            every { commentJpaPort.readComment(0,0,0) } returns null
//            every {
//                mockCommentService.readComment(
//                    CommentUseCase.Commend.ReadCommend(
//                        0,
//                        0,
//                        0,
//                        "test"
//                    )
//                )
//            } throws CommentDataNotFoundException(0,0,0)

            Then("It should throw CommentDataNotFoundException") {
                shouldThrowUnit<CommentDataNotFoundException> {
                    commentService.deleteComment(deleteCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "댓글이 존재하지 않습니다. [boardId: 0, postId: 0, commentId: 0]"
                }
            }
        }

        When("writer not match") {
            val readCommend = mockk<CommentUseCase.Commend.ReadCommend>()

            every { deleteCommend.boardId } returns 1
            every { deleteCommend.postId } returns 1
            every { deleteCommend.commentId } returns 1
            every { deleteCommend.modifier } returns "not match modifier"
            every { commentJpaPort.readComment(1, 1, 1) } returns comment
            every { comment.boardId } returns 1
            every { comment.postId } returns 1
            every { comment.commentId } returns 1
            every { readCommend.reader } returns "not match modifier"
            every { commentRedisPort.checkCommentLikeByEmail(1,1,1, "not match modifier") } returns false
            justRun { comment.updateIsLiked(!false) }
            every { comment.checkWriter("not match modifier") } throws WriterNotMatchException()

            Then("It should throw WriterNotMatchException") {
                shouldThrowUnit<WriterNotMatchException> {
                    commentService.deleteComment(deleteCommend)
                }.apply {
                    this.code shouldBe ErrorCode.INVALID_USER
                    this.message shouldBe "작성자만 수정이 가능합니다."
                }
            }
        }

        When("correct request") {
            val readCommend = mockk<CommentUseCase.Commend.ReadCommend>()

            every { deleteCommend.boardId } returns 1
            every { deleteCommend.postId } returns 1
            every { deleteCommend.commentId } returns 1
            every { deleteCommend.modifier } returns "not match modifier"
            every { commentJpaPort.readComment(1, 1, 1) } returns comment
            every { comment.boardId } returns 1
            every { comment.postId } returns 1
            every { comment.commentId } returns 1
            every { readCommend.reader } returns "not match modifier"
            every { commentRedisPort.checkCommentLikeByEmail(1,1,1, "not match modifier") } returns false
            justRun { comment.updateIsLiked(!false) }
            every { comment.checkWriter("not match modifier") } returns true
            justRun { commentJpaPort.deleteComment(1, 1, 1) }
            justRun { commentRedisPort.deleteCommentLikeAll(1, 1, 1) }

            commentService.deleteComment(deleteCommend)

            Then("It should delete comment and likes") {
                verify(exactly = 1) { commentJpaPort.deleteComment(1, 1, 1) }
                verify(exactly = 1) { commentRedisPort.deleteCommentLikeAll(1, 1, 1) }
            }
        }
    }

    given("delete all comment") {
        val deleteAllCommend = mockk<CommentUseCase.Commend.DeleteAllCommend>()

        When("correct request") {
            every { deleteAllCommend.boardId } returns 1
            every { deleteAllCommend.postId } returns 1
            justRun { commentKotlinJdslPort.deleteAllComment(1, 1) }
            justRun { commentRedisPort.deleteLikeCommentAllByPattern(1, 1) }

            commentService.deleteCommentAll(deleteAllCommend)

            Then("it should delete comments and likes") {
                verify(exactly = 1) { commentKotlinJdslPort.deleteAllComment(1, 1) }
                verify(exactly = 1) { commentRedisPort.deleteLikeCommentAllByPattern(1, 1) }
            }
        }
    }

})
