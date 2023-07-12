package com.example.spring.adapter.rest.member

import com.epages.restdocs.apispec.*
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.config.MemberDataNotFoundException
import com.example.spring.domain.member.Member
import com.example.spring.domain.member.MemberRole
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
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
    private lateinit var jwtService: JwtService

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
            MockMvcResultMatchers.jsonPath("memberId").exists(),
            MockMvcResultMatchers.jsonPath("email").exists(),
            MockMvcResultMatchers.jsonPath("role").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").value(nullOrString())
        ).andDocument(
            "POST-members-register",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Register member")
                    .description("Register member with email and password.")
                    .requestSchema(Schema("memberCreate.Request"))
                    .requestFields(
                        field("email", JsonFieldType.STRING, "Email of member", false),
                        field("password", JsonFieldType.STRING, "Password of member", false)
                    )
                    .responseHeaders(
                        header(HttpHeaders.LOCATION, "Location header")
                    )
                    .responseSchema(Schema("memberCreate.Response"))
                    .responseFields(
                        field("memberId", JsonFieldType.NUMBER, "Unique member ID", false),
                        field("email", JsonFieldType.STRING, "Email of member", false),
                        field("role", JsonFieldType.STRING, "Role of member", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of member", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of member", true)
                    )
                    .build()
            )
        )

        val jsonNode = ObjectMapper().readTree(result.andReturn().response.contentAsString)
        val createdMemberId = jsonNode["memberId"].asInt()

        memberJpaPort.deleteMember(createdMemberId)
        Assertions.assertThrows(MemberDataNotFoundException::class.java) {
            memberJpaPort.findMemberByMemberId(createdMemberId)
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
                snippetsBuilder()
                    .tag("members")
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

    @Test
    fun readMember() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val memberId = 1

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/members/{memberId}", memberId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("memberId").exists(),
            MockMvcResultMatchers.jsonPath("email").exists(),
            MockMvcResultMatchers.jsonPath("role").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").value(nullOrString())
        ).andDocument(
            "GET-members-{memberId}",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Read member")
                    .description("Read member with send info.")
                    .pathParameters(
                        parameter("memberId", SimpleType.NUMBER, "Unique member ID")
                    )
                    .responseSchema(Schema("memberRead.Response"))
                    .responseFields(
                        field("memberId", JsonFieldType.NUMBER, "Unique member ID", false),
                        field("email", JsonFieldType.STRING, "Email of member", false),
                        field("role", JsonFieldType.STRING, "Role of member", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of member", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of member", true)
                    )
                    .build()
            )
        )
    }

    @Test
    fun updateMember() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val memberId = 1
        val input = mutableMapOf<String, String>()
        input["password"] = "test"

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/members/{memberId}", memberId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("memberId").exists(),
            MockMvcResultMatchers.jsonPath("email").exists(),
            MockMvcResultMatchers.jsonPath("role").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists()
        ).andDocument(
            "PATCH-members-{memberId}",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Update member")
                    .description("Update member with send info.")
                    .pathParameters(
                        parameter("memberId", SimpleType.NUMBER, "Unique member ID")
                    )
                    .requestSchema(Schema("memberUpdate.Request"))
                    .requestFields(
                        field("password", JsonFieldType.STRING, "Password of member", false)
                    )
                    .responseSchema(Schema("memberUpdate.Response"))
                    .responseFields(
                        field("memberId", JsonFieldType.NUMBER, "Unique member ID", false),
                        field("email", JsonFieldType.STRING, "Email of member", false),
                        field("role", JsonFieldType.STRING, "Role of member", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of member", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of member", false)
                    )
                    .build()
            )
        )
    }

    @Test
    fun deleteMember() {
        val createdMember = memberJpaPort.createMember(
            Member(
                email = "testCreated",
                password = "1234",
                role = MemberRole.ROLE_STANDARD.name
            )
        )

        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(createdMember.email))
        val memberId = createdMember.memberId

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/members/{memberId}", memberId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isNoContent
        ).andDocument(
            "DELETE-members-{memberId}",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Delete member")
                    .description("Delete member with send info.")
                    .pathParameters(
                        parameter("memberId", SimpleType.NUMBER, "Unique member ID")
                    )
                    .build()
            )
        )

        Assertions.assertThrows(MemberDataNotFoundException::class.java) {
            memberJpaPort.findMemberByMemberId(memberId)
        }
    }

}