package com.example.spring.adapter.jpa.member.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityExistsException
import org.junit.jupiter.api.assertThrows

class MemberJpaEntityTest : StringSpec({

    "creating a member with valid values should not throw an exception" {
        val memberId = 1
        val email = "test@example.com"
        val password = "password123"
        val role = "ROLE_STANDARD"

        val member = MemberJpaEntity(memberId, email, password, role)

        member.memberId shouldBe memberId
        member.email shouldBe email
        member.password shouldBe password
        member.role shouldBe role

        val changeEmail = "mail2"
        val changePassword = "password"
        val changeRole = "ROLE_ADMIN"

        member.email = changeEmail
        member.password = changePassword
        member.role = changeRole

        member.email shouldBe changeEmail
        member.password shouldBe changePassword
        member.role shouldBe changeRole
    }
})
