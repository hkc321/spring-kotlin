package com.example.spring.member.adapter.`in`.web

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
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
        input["email"] = "test"
        input["pw"] = "test"

        val result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input)))

        // Then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string("email already exists"))
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "api/register"
                )
            )
    }

    @Test
    fun login() {
        val input = mutableMapOf<String, String>()
        input["email"] = "noID"
        input["pw"] = "noPW"


        // When
        var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input)))

        // Then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("success", `is`("false")))
            .andExpect(MockMvcResultMatchers.jsonPath("message", startsWith("login fail")))
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "api/login/bad_credential"
                )
            )


        val input2 = mutableMapOf<String, String>()
        input2["email"] = "test"
        input2["pw"] = "test"

        // When
        var result2 = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(input2)))

        // Then
        result2
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("success", `is`("true")))
            .andExpect(MockMvcResultMatchers.jsonPath("message", startsWith("login success")))
            .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
            .andExpect(MockMvcResultMatchers.header().exists("Authorization-refresh"))
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "api/login/success"
                )
            )
    }

    @Test
    fun test1() {
    }
}