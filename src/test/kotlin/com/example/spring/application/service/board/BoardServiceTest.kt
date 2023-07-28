package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.port.out.board.BoardKotlinJdslPort
import com.example.spring.application.service.board.exception.BoardDataNotFoundException
import com.example.spring.application.service.board.exception.BoardExistException
import com.example.spring.config.code.ErrorCode
import com.example.spring.domain.board.Board
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.springframework.dao.DataIntegrityViolationException

class BoardServiceTest : BehaviorSpec({
    val boardJpaPort = mockk<BoardJpaPort>()
    val boardKotlinJdslPort = mockk<BoardKotlinJdslPort>()

    val boardService = BoardService(boardJpaPort, boardKotlinJdslPort)

    given("Create board") {
        val createCommend = mockk<BoardUseCase.Commend.CreateCommend>()

        When("name already exist") {
            every { createCommend.name } returns "토론게시판"
            every { createCommend.description } returns "password"
            every { createCommend.writer } returns "test"
            every { boardJpaPort.createBoard(any()) } throws DataIntegrityViolationException("test")

            Then("it should throw BoardExistException") {
                shouldThrowUnit<BoardExistException> {
                    boardService.createBoard(createCommend)
                }.apply {
                    this.code shouldBe ErrorCode.ALREADY_EXIST
                    this.message shouldBe "같은 이름의 게시판이 존재합니다."
                }
            }
        }
    }

    given("Update board") {
        val updateCommend = mockk<BoardUseCase.Commend.UpdateCommend>()

        When("board not exist") {
            every { boardKotlinJdslPort.readBoard(any()) } returns null
            every { updateCommend.boardId } returns 31


            Then("it should throw BoardDataNotFoundException") {
                shouldThrowUnit<BoardDataNotFoundException> {
                    boardService.updateBoard(updateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.DATA_NOT_FOUND
                    this.message shouldBe "게시판이 존재하지 않습니다. [boardId: 31]"
                }
            }
        }

        When("name already exist") {
            val board = mockk<Board>()
            every { boardKotlinJdslPort.readBoard(any()) } returns board
            every { updateCommend.boardId } returns 31
            every { updateCommend.name } returns "test"
            every { updateCommend.description } returns "test"
            every { updateCommend.modifier } returns "test"
            justRun { board.update(any(), any(), any()) }
            every { boardJpaPort.updateBoard(board) } throws DataIntegrityViolationException("test")


            Then("it should throw BoardExistException") {
                shouldThrowUnit<BoardExistException> {
                    boardService.updateBoard(updateCommend)
                }.apply {
                    this.code shouldBe ErrorCode.ALREADY_EXIST
                    this.message shouldBe "같은 이름의 게시판이 존재합니다."
                }
            }
        }
    }
})
