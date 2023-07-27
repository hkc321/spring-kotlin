package com.example.spring.config.dto

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JwtExceptionResponseTest : StringSpec({
    "JwtExceptionResponse should have correct properties" {
        val errorCode = "INVALID_TOKEN"
        val errorMessage = "The provided JWT token is invalid."

        val response = JwtExceptionResponse(errorCode, errorMessage)

        response.errorCode shouldBe errorCode
        response.errorMessage shouldBe errorMessage
    }

    "JwtExceptionResponse objects with the same properties should be equal" {
        val errorCode = "INVALID_TOKEN"
        val errorMessage = "The provided JWT token is invalid."

        val response1 = JwtExceptionResponse(errorCode, errorMessage)
        val response2 = JwtExceptionResponse(errorCode, errorMessage)

        response1 shouldBe response2
    }

    "JwtExceptionResponse.toString() should return the correct string representation" {
        val errorCode = "INVALID_TOKEN"
        val errorMessage = "The provided JWT token is invalid."

        val response = JwtExceptionResponse(errorCode, errorMessage)

        val expectedString = "JwtExceptionResponse(errorCode=$errorCode, errorMessage=$errorMessage)"
        response.toString() shouldBe expectedString
    }

})
