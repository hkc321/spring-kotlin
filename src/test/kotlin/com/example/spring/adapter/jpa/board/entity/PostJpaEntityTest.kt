package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PostJpaEntityTest : StringSpec({
    "creating a post with valid values should not throw an exception" {
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

        post.postId shouldBe postId
        post.board shouldBe postBoard
        post.title shouldBe postTitle
        post.content shouldBe postContent
        post.writer shouldBe postWriter

        val changePostBoard = BoardJpaEntity(12, name, description, writer, modifier)
        val changePostTitle = "test1"
        val changePostContent = "test1"
        val changePostWriter = MemberJpaEntity(13, email, password, role)

        post.board = changePostBoard
        post.title = changePostTitle
        post.content = changePostContent
        post.writer = changePostWriter

        post.board shouldBe changePostBoard
        post.title shouldBe changePostTitle
        post.content shouldBe changePostContent
        post.writer shouldBe changePostWriter

    }

})
