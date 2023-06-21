package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.application.service.member.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
class CommentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var commentJpaPort: CommentJpaPort

    private val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    @Transactional
    fun createComment() {
        val token = jwtService.createAccessToken("test")
        val input = mutableMapOf<String, Any?>()
        input["boardId"] = 23
        input["parentCommentId"] = null
        input["level"] = 1
        input["content"] = "test"
        input["writer"] = "test"

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/comment")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("commentId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("parentCommentId").value(
                Matchers.anyOf(
                    Matchers.nullValue(),
                    Matchers.instanceOf(Int::class.javaObjectType)
                )
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("level").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("content").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("up").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("updatedAt").value(null))
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "success_POST_comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    snippets = arrayOf(
                        ResourceDocumentation.resource(
                            ResourceSnippetParameters.builder()
                                .summary("Create comment")
                                .description("Create comment with send info")
                                .requestSchema(Schema("Request.commentCreate"))
                                .responseSchema(Schema("Response.comment"))
                                .requestFields(
                                    PayloadDocumentation.fieldWithPath("boardId").type(JsonFieldType.NUMBER)
                                        .description("Unique board ID"),
                                    PayloadDocumentation.fieldWithPath("parentCommentId").optional().type(JsonFieldType.NUMBER)
                                        .description("ParentCommentId of comment"),
                                    PayloadDocumentation.fieldWithPath("level").type(JsonFieldType.NUMBER)
                                        .description("Level of comment"),
                                    PayloadDocumentation.fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("Content of comment"),
                                    PayloadDocumentation.fieldWithPath("writer").type(JsonFieldType.STRING)
                                        .description("Writer of comment"),
                                )
                                .responseFields(
                                    PayloadDocumentation.fieldWithPath("commentId").type(JsonFieldType.NUMBER)
                                        .description("Unique comment ID"),
                                    PayloadDocumentation.fieldWithPath("boardId").type(JsonFieldType.NUMBER)
                                        .description("Unique board ID"),
                                    PayloadDocumentation.fieldWithPath("parentCommentId").optional().type(JsonFieldType.STRING)
                                        .description("ParentCommentId of comment"),
                                    PayloadDocumentation.fieldWithPath("level").type(JsonFieldType.NUMBER)
                                        .description("Level of comment"),
                                    PayloadDocumentation.fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("Content of comment"),
                                    PayloadDocumentation.fieldWithPath("up").type(JsonFieldType.NUMBER)
                                        .description("Count of who click like"),
                                    PayloadDocumentation.fieldWithPath("writer").type(JsonFieldType.STRING)
                                        .description("Writer of comment"),
                                    PayloadDocumentation.fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                        .description("Created datetime of comment"),
                                    PayloadDocumentation.fieldWithPath("updatedAt").type(JsonFieldType.VARIES)
                                        .description("Updated datetime of comment"),
                                )
                                .build()
                        )
                    )
                )
            )

//        commentJpaPort.deleteComment(JSONObject(result.andReturn().response.contentAsString).getInt("commentId"))
    }

    @Test
    fun readComment() {
    }

    @Test
    fun readChildComment() {
    }

    @Test
    fun updateComment() {
    }

    @Test
    fun deleteComment() {
    }
}