package com.example.spring.adapter.rest.member

import com.epages.restdocs.apispec.*
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.config.MemberDataNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class MemberControllerDocsTest : RestdocsTestDsl {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberJpaPort: MemberJpaPort


    @Test
    fun register() {
        val input = mutableMapOf<String, String>()
        input["email"] = "testtest"
        input["password"] = "testtest"

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        // then
        result.andExpectAll(
            MockMvcResultMatchers.status().isCreated,
            MockMvcResultMatchers.jsonPath("email").exists()
        ).andDocument(
            "POST-members-register",
            snippets = makeSnippets(
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
                    .responseSchema(Schema("memberCreate.Response"))
                    .responseFields(
                        field("email", JsonFieldType.STRING, "Email of member", false)
                    )
                    .build()
            )
        )

        val jsonNode = ObjectMapper().readTree(result.andReturn().response.contentAsString)
        val createdEmail= jsonNode["email"].asText()

        memberJpaPort.deleteMember(createdEmail)
        Assertions.assertThrows(MemberDataNotFoundException::class.java) {
            memberJpaPort.findMemberByEmail(createdEmail)
        }

    }

    @Test
    fun login() {
        val input = mutableMapOf<String, String>()
        input["email"] = "test"
        input["password"] = "test"

        // When
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        // Then
        result.andDocument(
            "POST-members-login",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .summary("Login member")
                    .description("Login with email and password.")
                    .requestSchema(Schema("memberLogin.Request"))
                    .requestFields(
                        field("email", JsonFieldType.STRING, "Email of member", false),
                        field("password", JsonFieldType.STRING, "Password of member", false)
                    )
                    .responseHeaders(
                        header("Authorization", "access token"),
                        header("Authorization-refresh", "refresh token")
                    )
                    .responseSchema(Schema("memberLogin.Response"))
                    .responseFields(
                        field("success", JsonFieldType.BOOLEAN, "Success or not", false),
                        field("message", JsonFieldType.STRING, "Message from server", false)
                    )
                    .build()
            )
        )
    }

}