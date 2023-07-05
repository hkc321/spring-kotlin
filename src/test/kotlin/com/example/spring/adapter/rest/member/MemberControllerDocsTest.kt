package com.example.spring.member.adapter.`in`.web

import com.epages.restdocs.apispec.*
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.config.BoardDataNotFoundException
import com.example.spring.config.MemberDataNotFoundException
import com.example.spring.domain.member.MemberRole
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.file.exist
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
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.*
import org.springframework.restdocs.request.PathParametersSnippet
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.JsonPathResultMatchersDsl

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerDocsTest : AnnotationSpec(), RestdocsTestDsl {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var memberJpaPort: MemberJpaPort
    override fun extensions() = listOf(SpringExtension)


    @Test
    fun `register`() {
        val input = mutableMapOf<String, String>()
        input["email"] = "testtest"
        input["password"] = "testtest"

        // when
        val result = mockMvc.post("/members/register") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(input)
        }

        //then
        result.andExpect {
            status { isCreated() }
            jsonPath("email", exist())
        }.andDocument(
            "POST-members",
            snippets = arrayOf(
                ResourceDocumentation.resource(
                    ResourceSnippetParameters.builder()
                        .summary("Register member")
                        .description("Register member with email and password.")
                        .requestSchema(Schema("memberCreate.Request"))
                        .requestFields(
                            field("email", JsonFieldType.STRING, "Email of member", false),
                            field("password", JsonFieldType.STRING, "Password of member", false),
                            field(
                                "role",
                                JsonFieldType.STRING,
                                "Role of Member. Only ROLE_STANDARD or ROLE_ADMIN allowed. Default is ROLE_STANDARD",
                                true
                            )
                        )
                        .responseHeaders(
                            header("Authorization", "access token"),
                            header("Authorization-refresh", "refresh token")
                        )
                        .responseSchema(Schema("memberCreate.Response"))
                        .responseFields(
                            field("success", JsonFieldType.BOOLEAN, "Success or not", false),
                            field("message", JsonFieldType.STRING, "Message from server", false)
                        )
                        .build()
                )
            )
        )
        memberJpaPort.deleteMember(input["email"]!!)
        Assertions.assertThrows(MemberDataNotFoundException::class.java) {
            memberJpaPort.findMemberByEmail(input["email"]!!)
        }

    }


    @Test
    fun `login`() {
        val input = mutableMapOf<String, String>()
        input["email"] = "test"
        input["password"] = "test"

        // When
        var result = mockMvc.post("/members/login") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(input)
        }

        // Then
        result.andExpect {
            status { isOk() }
            jsonPath("success", `is`(true))
            jsonPath("message", startsWith("login success"))
            header { exists("Authorization") }
            header { exists("Authorization-refresh") }
        }.andDocument(
            "POST-members-login",
            snippets = arrayOf(
                ResourceDocumentation.resource(
                    ResourceSnippetParameters.builder()
                        .summary("Login member")
                        .description("Update board detail")
                        .requestSchema(Schema("Request.boardUpdate"))
                        .requestFields(
                            field("email", JsonFieldType.STRING, "Email of member", false),
                            field("password", JsonFieldType.STRING, "Password of member", false)
                        )
                        .responseHeaders(
                            header("Authorization", "access token"),
                            header("Authorization-refresh", "refresh token")
                        )
                        .responseSchema(Schema("Response.board"))
                        .responseFields(
                            field("success", JsonFieldType.BOOLEAN, "Success or not", false),
                            field("message", JsonFieldType.STRING, "Message from server", false)
                        )
                        .build()
                )
            )
        )
    }

}