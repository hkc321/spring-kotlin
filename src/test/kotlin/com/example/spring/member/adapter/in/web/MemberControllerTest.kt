package com.example.spring.member.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.hamcrest.CoreMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun register() {
        val input = mutableMapOf<String, String>()
        input["id"] = "test"
        input["pw"] = "test"

        val result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input)))

        // Then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string("ID already exists"))
            .andDo(
                document(
                    "api/register"
                )
            )
    }

    @Test
    fun login() {
        val input = mutableMapOf<String, String>()
        input["id"] = "noID"
        input["pw"] = "noPW"


        // When
        var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input)))

        // Then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string("user not found"))
            .andDo(
                document(
                    "api/login/wrong_id"
                )
            )

        val input2 = mutableMapOf<String, String>()
        input2["id"] = "test"
        input2["pw"] = "noPW"

        // When
        var result2 = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input2)))

        // Then
        result2
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string("invalid password"))
            .andDo(
                document(
                    "api/login/wrong_pw"
                )
            )


        val input3 = mutableMapOf<String, String>()
        input3["id"] = "test"
        input3["pw"] = "test"

        // When
        var result3 = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input3)))

        // Then
        result3
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.X-AUTH-TOKEN-ACCESS", notNullValue()))
            .andDo(
                document(
                    "api/login/success"
                )
            )
    }

    @Test
    fun test1() {
    }
}