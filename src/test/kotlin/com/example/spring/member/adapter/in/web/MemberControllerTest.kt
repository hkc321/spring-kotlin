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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

//    @Test
//    fun register() {
//        val input = mutableMapOf<String, String>()
//        input["email"] = "test"
//        input["pw"] = "test"
//
//        val result = mockMvc.perform(
//            RestDocumentationRequestBuilders.post("/member/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectMapper().writeValueAsString(input))
//        )
//
//        // Then
//        result
//            .andExpect(MockMvcResultMatchers.status().isBadRequest)
//            .andExpect(MockMvcResultMatchers.content().string("email already exists"))
//            .andDo(
//                MockMvcRestDocumentationWrapper.document(
//                    "member/register"
//                )
//            )
//    }
//
//    @Test
//    fun loginFail() {
//        val input = mutableMapOf<String, String>()
//        input["email"] = "noID"
//        input["pw"] = "noPW"
//
//
//        // When
//        var result = mockMvc.perform(
//            RestDocumentationRequestBuilders.post("/member/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectMapper().writeValueAsString(input))
//        )
//
//        // Then
//        result
//            .andExpect(MockMvcResultMatchers.status().isBadRequest)
//            .andExpect(MockMvcResultMatchers.jsonPath("success", `is`("false")))
//            .andExpect(MockMvcResultMatchers.jsonPath("message", startsWith("login fail")))
//            .andDo(
//                MockMvcRestDocumentationWrapper.document(
//                    "member/login/bad_credential"
//                )
//            )
//            .andDo(
//                MockMvcRestDocumentation.document(
//                    "member/login/bad_credential",
//                    PayloadDocumentation.requestFields(
//                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
//                            .description("Member email"),
//                        PayloadDocumentation.fieldWithPath("pw").type(JsonFieldType.STRING)
//                            .description("Member password"),
//                    ),
//                    PayloadDocumentation.responseFields(
//                        PayloadDocumentation.fieldWithPath("success").type(JsonFieldType.STRING)
//                            .description("Success or not"),
//                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
//                            .description("Message from server"),
//                    )
//                )
//            )
//    }
//
//    @Test
//    fun loginSuccess() {
//        val input2 = mutableMapOf<String, String>()
//        input2["email"] = "test"
//        input2["pw"] = "test"
//
//        // When
//        var result2 = mockMvc.perform(
//            RestDocumentationRequestBuilders.post("/member/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectMapper().writeValueAsString(input2))
//        )
//
//        // Then
//        result2
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("success", `is`("true")))
//            .andExpect(MockMvcResultMatchers.jsonPath("message", startsWith("login success")))
//            .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
//            .andExpect(MockMvcResultMatchers.header().exists("Authorization-refresh"))
//            .andDo(
//                MockMvcRestDocumentationWrapper.document(
//                    "member/login/success"
//                )
//            )
//            .andDo(
//                MockMvcRestDocumentation.document(
//                    "member/login/success",
//                    PayloadDocumentation.requestFields(
//                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
//                            .description("Member email"),
//                        PayloadDocumentation.fieldWithPath("pw").type(JsonFieldType.STRING)
//                            .description("Member password"),
//                    ),
//                    PayloadDocumentation.responseFields(
//                        PayloadDocumentation.fieldWithPath("success").type(JsonFieldType.STRING)
//                            .description("Success or not"),
//                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
//                            .description("Message from server"),
//                    )
//                )
//            )
//    }

}