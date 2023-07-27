package com.example.spring.adapter.jpa.board.entity

import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BoardJpaEntityTest : StringSpec({
    "creating a board with valid values should not throw an exception" {
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

        board.boardId shouldBe boardId
        board.name shouldBe name
        board.description shouldBe description
        board.writer shouldBe writer
        board.modifier shouldBe modifier


        val changeName = "mail2"
        val changeDescription = "password"
        val changeWriter = MemberJpaEntity(2, email, password, role)
        val changeModifier = MemberJpaEntity(3, email, password, role)

        board.name = changeName
        board.description = changeDescription
        board.writer = changeWriter
        board.modifier = changeModifier


        board.name shouldBe changeName
        board.description shouldBe changeDescription
        board.writer shouldBe changeWriter
        board.modifier shouldBe changeModifier

    }

})
