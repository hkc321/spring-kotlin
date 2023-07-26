package com.example.spring.config.controller


import com.example.spring.application.service.board.exception.*
import com.example.spring.application.service.member.exception.JwtRenewException
import com.example.spring.application.service.member.exception.MemberAccessorNotMatchException
import com.example.spring.application.service.member.exception.MemberAlreadyExistException
import com.example.spring.application.service.member.exception.MemberDataNotFoundException
import com.example.spring.config.code.ErrorCode
import com.example.spring.config.dto.BaseExceptionResponse
import com.example.spring.config.exception.WriterNotMatchException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import kotlin.reflect.KParameter

class ControllerAdviceTest : DescribeSpec({

    describe("ControllerAdvice") {
        val controllerAdvice = ControllerAdvice()

        it("should handle JwtRenewException") {
            val ex = JwtRenewException(HttpStatus.BAD_REQUEST, ErrorCode.JWT_EXCEPTION, "JWT ERROR")
            val response = controllerAdvice.jwtRenewException(ex)

            response.statusCode shouldBe ex.status
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle JsonParseException") {
            val ex = JsonParseException("JSON 형식이 잘못되었습니다.")
            val response = controllerAdvice.jsonParseException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ErrorCode.INVALID_FORMAT, ex.message.toString())
        }

        it("should handle MissingServletRequestParameterException") {
            val ex = MissingServletRequestParameterException("name", "type")
            val message = "Parameter is missing: [name(type)]"
            val response = controllerAdvice.missingServletRequestParameterException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ErrorCode.INVALID_PARAMETER, message)
        }

        it("should handle CommentLikeException") {
            val ex = CommentLikeException(boardId = 1, postId = 1, commentId = 1, code = ErrorCode.DATA_NOT_FOUND, "이미 좋아요를 클릭한 댓글입니다. [boardId: 1, postId: 1, commentId: 1]")
            val response = controllerAdvice.commentLikeException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle PostLikeException") {
            val ex = PostLikeException(boardId = 1, postId = 1, code = ErrorCode.DATA_NOT_FOUND, "좋아요를 클릭한 이력이 없습니다. [boardId: 1, postId: 1]")
            val response = controllerAdvice.postLikeException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle MemberAccessorNotMatchException") {
            val ex = MemberAccessorNotMatchException()
            val response = controllerAdvice.memberAccessorNotMatchException(ex)

            response.statusCode shouldBe HttpStatus.FORBIDDEN
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle WriterNotMatchException") {
            val ex = WriterNotMatchException()
            val response = controllerAdvice.writerNotMatchException(ex)

            response.statusCode shouldBe HttpStatus.FORBIDDEN
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle HttpRequestMethodNotSupportedException") {
            val method = "POST"
            val message = "Request method '$method' is not supported"
            val ex = HttpRequestMethodNotSupportedException(method)
            val response = controllerAdvice.httpRequestMethodNotSupportedException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ErrorCode.NOT_SUPPORT_HTTP_METHOD, message)
        }

        it("should handle ConstraintViolationException") {
            val violation1 = mockk<ConstraintViolation<*>>()

            val constraintViolations = setOf(violation1)
            val errorMessage = "Error 1"
            every { violation1.message } returns "Error 1"
            every { violation1.propertyPath.toString() } returns "property1"

            val ex = ConstraintViolationException(constraintViolations)
            val response = controllerAdvice.constraintViolationException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ErrorCode.INVALID_PARAMETER, errorMessage.trim())
        }

//        it("should handle HttpMessageNotReadableException with InvalidFormatException") {
//            val invalidValue = "invalid-value"
//            val field = mockk<Field>()
//            val targetTypeFields = arrayOf(
//                field.apply { every { name } returns "field1" },
//                field.apply { every { name } returns "field2" }
//            )
//
//
//            val targetType = mockk<InvalidFormatException>()
//            every { targetType.value } returns invalidValue
//            every { targetType.targetType.fields } returns targetTypeFields
//
//
//            val ex = HttpMessageNotReadableException("Invalid message", targetType)
//            val errorMessage = "입력 받은 [$invalidValue] 를 변환중 에러가 발생했습니다. 오직 [field1, field2] 만 가능합니다"
//
//            val response = controllerAdvice.httpMessageNotReadableException(ex)
//
//
//            response.statusCode shouldBe HttpStatus.BAD_REQUEST
//            response.body shouldBe BaseExceptionResponse(ErrorCode.INVALID_PARAMETER, errorMessage)
//        }

        it("should handle HttpMessageNotReadableException with MissingKotlinParameterException") {
            val paramName = mockk<KParameter>()
            val missingKotlinParameterException = MissingKotlinParameterException(
                paramName,
                null,
                "test"
            )
            val ex = HttpMessageNotReadableException("Missing parameter", missingKotlinParameterException)
            val expectedErrorMessage = "Parameter is missing: [test]"
            every { paramName.name } returns "test"
            val response = controllerAdvice.httpMessageNotReadableException(ex)


            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ErrorCode.INVALID_PARAMETER, expectedErrorMessage)
        }

        it("should handle other HttpMessageNotReadableException") {
            val ex = HttpMessageNotReadableException("Other error")
            val expectedErrorMessage = "파라미터를 확인해주세요"
            val response = controllerAdvice.httpMessageNotReadableException(ex)


            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ErrorCode.INVALID_PARAMETER, expectedErrorMessage)
        }

        it("handle MethodArgumentTypeMismatchException") {
            val ex = mockk<MethodArgumentTypeMismatchException>()
            val errorCode = ErrorCode.INVALID_PARAMETER
            val invalidValue = "invalid-value"
            val argumentName = "argName"
            val requiredType = Int::class.java // Replace with the expected required type

            every { ex.errorCode } returns "errorCode" // Replace with the actual error code value
            every { ex.value } returns invalidValue
            every { ex.name } returns argumentName
            every { ex.requiredType } returns requiredType

            val controllerAdvice = ControllerAdvice()
            val response = controllerAdvice.methodArgumentTypeMismatchException(ex)

            val expectedMessage =
                "[errorCode: $invalidValue] - $argumentName required $requiredType"

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(errorCode, expectedMessage)
        }

        it("should handle MethodArgumentNotValidException") {
            val methodParameter = mockk<MethodParameter>()
            val bindingResult = mockk<BindingResult>()

            val fieldError = FieldError("", "a", "b", true, null, null, "c")
            every { bindingResult.fieldErrors } returns mutableListOf(fieldError)

            val errorCode = ErrorCode.INVALID_PARAMETER
            val errorMessage = "[a](은)는 c 입력된 값: [b]"

            val ex = MethodArgumentNotValidException(methodParameter, bindingResult)
            val response = controllerAdvice.methodArgumentNotValidException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(errorCode, errorMessage)
        }

        it("should handle CommentDataNotFoundException") {
            val ex = CommentDataNotFoundException(boardId = 1, postId = 1, commentId = 1)
            val response = controllerAdvice.commentDataNotFoundException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle PostDataNotFoundException") {
            val ex = PostDataNotFoundException(boardId = 1, postId = 1)
            val response = controllerAdvice.postDataNotFoundException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle BoardDataNotFoundException") {
            val ex = BoardDataNotFoundException(boardId = 1)
            val response = controllerAdvice.boardDataNotFoundException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle MemberDataNotFoundException") {
            val ex = MemberDataNotFoundException()
            val response = controllerAdvice.memberDataNotFoundException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }

        it("should handle MemberAlreadyExistException") {
            val ex = MemberAlreadyExistException()
            val response = controllerAdvice.memberAlreadyExistException(ex)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe BaseExceptionResponse(ex.code, ex.message)
        }


        it("should handle other exceptions with INTERNAL_SERVER_ERROR") {
            val ex = Exception("Some internal error")
            val response = controllerAdvice.exception(ex)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            response.body shouldBe BaseExceptionResponse(ErrorCode.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR")
        }
    }

})
