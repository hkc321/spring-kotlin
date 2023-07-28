package com.example.spring.application.service.member

import com.example.spring.application.port.`in`.member.MemberUseCase
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.exception.MemberAlreadyExistException
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.config.code.ErrorCode
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class MemberServiceTest : BehaviorSpec({
    val memberJpaPort = mockk<MemberJpaPort>()
    val jwtService = mockk<JwtService>()
    val passwordEncoder = mockk<BCryptPasswordEncoder>()


    val memberService = MemberService(memberJpaPort, passwordEncoder, jwtService)



    given("Create member") {
        val createCommend = mockk<MemberUseCase.Commend.CreateCommend>()

        When("email already exist when create member") {
            every { createCommend.email } returns "test"
            every { createCommend.password } returns "password"
            every { passwordEncoder.encode("password") } returns "wqe@$#s"
            every { memberJpaPort.createMember(any()) } throws DataIntegrityViolationException("test")

            Then("it should throw MemberAlreadyExistException") {
                shouldThrowUnit<MemberAlreadyExistException> {
                    memberService.createMember(createCommend)
                }.apply {
                    this.code shouldBe ErrorCode.ALREADY_EXIST
                    this.message shouldBe "이미 존재하는 아이디입니다."
                }
            }
        }
    }

    given("Read member") {
        val readCommend = mockk<MemberUseCase.Commend.ReadCommend>()

        When("member is not exist") {
            every { readCommend.memberId } returns 1
            every { memberJpaPort.findMemberByMemberId(1) } returns null

            Then("it should throw MemberDataNotFoundException") {
                shouldThrowUnit<MemberDataNotFoundException> {
                    memberService.readMember(readCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "사용자가 존재하지 않습니다."
                }
            }
        }
    }

    given("Update member") {
        val updateCommend = mockk<MemberUseCase.Commend.UpdateCommend>()

        When("member is not exist") {
            every { updateCommend.memberId } returns 2
            every { memberJpaPort.findMemberByMemberId(2) } returns null

            Then("it should throw MemberDataNotFoundException") {
                shouldThrowUnit<MemberDataNotFoundException> {
                    memberService.updateMember(updateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "사용자가 존재하지 않습니다."
                }
            }
        }
    }

    given("Update member role") {
        val updateRoleCommend = mockk<MemberUseCase.Commend.UpdateRoleCommend>()

        When("member is not exist") {
            every { updateRoleCommend.memberId } returns 3
            every { memberJpaPort.findMemberByMemberId(3) } returns null

            Then("it should throw MemberDataNotFoundException") {
                shouldThrowUnit<MemberDataNotFoundException> {
                    memberService.updateMemberRole(updateRoleCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "사용자가 존재하지 않습니다."
                }
            }
        }
    }

    given("Delete member") {
        val deleteCommend = mockk<MemberUseCase.Commend.DeleteCommend>()

        When("member does not exist when delete member") {
            every { deleteCommend.memberId } returns 1
            every { memberJpaPort.findMemberByMemberId(1) } returns null

            Then("it should throw MemberDataNotFoundException") {
                shouldThrowUnit<MemberDataNotFoundException> {
                    memberService.deleteMember(deleteCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "사용자가 존재하지 않습니다."
                }
            }
        }
    }

})
