package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CommentJpaEntityTest : StringSpec({
    "creating a comment with valid values should not throw an exception" {
        val memberId = 1
        val email = "test@example.com"
        val password = "password123"
        val role = "ROLE_STANDARD"

        val member = MemberJpaEntity(memberId, email, password, role)

        val boardId = 2
        val name = "test"
        val description = "test"
        val writer = member
        val modifier = member

        val board = BoardJpaEntity(boardId, name, description, writer, modifier)

        val postId = 1
        val postBoard = board
        val postTitle = "test"
        val postContent = "test"
        val postWriter = member

        val post = PostJpaEntity(postId, postBoard, postTitle, postContent, postWriter)

        val commentId = 1
        val commentBoard = board
        val commentPost = post
        val commentParentComment = null
        val commentLevel = 1
        val commentContent = "test"
        val commentWriter = member

        val comment = CommentJpaEntity(commentId, commentBoard, commentPost, commentParentComment, commentLevel, commentContent, commentWriter)

        comment.commentId shouldBe commentId
        comment.board shouldBe commentBoard
        comment.post shouldBe commentPost
        comment.parentComment shouldBe commentParentComment
        comment.level shouldBe commentLevel
        comment.content shouldBe commentContent
        comment.writer shouldBe commentWriter

        val changeCommentBoard = BoardJpaEntity(13, name, description, writer, modifier)
        val changeCommentPost = PostJpaEntity(postId, postBoard, postTitle, postContent, postWriter)
        val changeCommentParentComment = comment
        val changeCommentLevel = 2
        val changeCommentContent = "test2"
        val changeCommentWriter = MemberJpaEntity(24, email, password, role)

        comment.board = changeCommentBoard
        comment.post = changeCommentPost
        comment.parentComment = changeCommentParentComment
        comment.level = changeCommentLevel
        comment.content = changeCommentContent
        comment.writer = changeCommentWriter

        comment.board shouldBe changeCommentBoard
        comment.post shouldBe changeCommentPost
        comment.parentComment shouldBe changeCommentParentComment
        comment.level shouldBe changeCommentLevel
        comment.content shouldBe changeCommentContent
        comment.writer shouldBe changeCommentWriter
    }

})
