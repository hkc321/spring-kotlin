package com.example.spring.adapter.rest.member

import com.epages.restdocs.apispec.*
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.domain.member.Member
import com.example.spring.domain.member.MemberRole
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
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
import org.springframework.transaction.annotation.Transactional

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
    @Transactional
    fun register() {
        val input = mutableMapOf<String, String>()
        input["email"] = "testtest@gmail.com"
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
                    .description("이메일과 비밀번호를 입력하여 회원가입을 진행합니다. 이메일은 중복이 불가능합니다.")
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

        assert(memberJpaPort.findMemberByMemberId(createdMemberId) == null)
    }

    @Test
    @Transactional
    fun login() {
        val email = "test"
        val input = mutableMapOf<String, String>()
        input["email"] = email
        input["password"] = "test"

        // When
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        // Then
        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("success").exists(),
            MockMvcResultMatchers.jsonPath("member").isMap,
        ).andDocument(
            "POST-members-login",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Login member")
                    .description("이메일과 비밀번호를 입력하여 로그인을 진행합니다.")
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
                        field("member.memberId", JsonFieldType.NUMBER, "Unique member ID", false),
                        field("member.email", JsonFieldType.STRING, "Email of member", false),
                        field("member.role", JsonFieldType.STRING, "Role of member", false),
                        field("member.createdAt", JsonFieldType.STRING, "Created datetime of member", false),
                        field("member.updatedAt", JsonFieldType.STRING, "Updated datetime of member", true)
                    )
                    .build()
            )
        )
        jwtService.deleteRefreshTokenByEmail(email)
    }

    @Test
    fun readMember() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val memberId = 1

        //when
        val result = mockMvc.perform(
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
                    .description("회원의 정보를 조회합니다. 본인의 정보만 접근이 가능합니다.")
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
    @Transactional
    fun updateMember() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val memberId = 1
        val input = mutableMapOf<String, String>()
        input["password"] = "test"

        //when
        val result = mockMvc.perform(
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
                    .description("회원의 정보를 업데이트 합니다. 오직 자신의 정보만 업데이트 가능합니다.")
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
    @Transactional
    fun updateMemberRole() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test_admin")!!)
        val memberId = 2
        val input = mutableMapOf<String, String>()
        input["role"] = "ROLE_ADMIN"

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/members/{memberId}/role", memberId)
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
            "PATCH-members-{memberId}-role",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Update member role")
                    .description("회원의 권한을 업데이트 합니다. 오직 관리자(ROLE_ADMIN) 권한을 가진 사람만 변경 가능합니다.")
                    .pathParameters(
                        parameter("memberId", SimpleType.NUMBER, "Unique member ID")
                    )
                    .requestSchema(Schema("memberUpdateRole.Request"))
                    .requestFields(
                        field("role", JsonFieldType.STRING, "Role of member", false)
                    )
                    .responseSchema(Schema("memberUpdateRole.Response"))
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
    @Transactional
    fun deleteMember() {
        val createdMember = memberJpaPort.createMember(
            Member(
                email = "testCreated",
                password = "1234",
                role = MemberRole.ROLE_STANDARD.name
            )
        )

        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(createdMember.email)!!)
        val memberId = createdMember.memberId

        //when
        val result = mockMvc.perform(
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
                    .description("회원을 제거합니다. 오직 본인만이 삭제 가능합니다.")
                    .pathParameters(
                        parameter("memberId", SimpleType.NUMBER, "Unique member ID")
                    )
                    .build()
            )
        )

        assert(memberJpaPort.findMemberByMemberId(memberId) == null)
    }

    @Test
    @Transactional
    fun logout() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)

        // When
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/members/logout")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        // Then
        result.andExpectAll(
            MockMvcResultMatchers.status().isOk
        ).andDocument(
            "POST-members-logout",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Logout member")
                    .description("로그아웃을 진행합니다.")
                    .responseSchema(Schema("memberLogout.Response"))
                    .responseFields(
                        field("success", JsonFieldType.BOOLEAN, "success or not", true)
                    )
                    .build()
            )
        )
        jwtService.deleteLogoutToken(token)
    }

    @Test
    @Transactional
    fun renewToken() {
        val email = "test"
        val member = memberJpaPort.findMemberByEmail(email)!!
        val accessToken = jwtService.createAccessToken(member)
        val refreshToken = jwtService.createRefreshToken(member)
        val time = jwtService.extractClaims(refreshToken).expiration.time
        jwtService.saveRefreshToken(email, refreshToken, time)

        // When
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/members/token")
                .header("Authorization", "Bearer $accessToken")
                .header("Authorization-refresh", "Bearer $refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
        )

        // Then
        result.andExpectAll(
            MockMvcResultMatchers.status().isOk
        ).andDocument(
            "POST-members-token",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("members")
                    .summary("Renew token.")
                    .description("토큰을 갱신합니다. access token이 만료되면, 헤더에 refresh token을 담아 access token을 갱신합니다. refresh token의 만료기간이 7일 미만이라면, refresh token 또한 갱신됩니다. 갱신된 토큰들은 response header에서 확인 가능합니다.")
                    .requestHeaders(
                        header("Authorization-refresh", "Refresh token", false)
                    )
                    .responseHeaders(
                        header("Authorization", "Renewed access token", false),
                        header("Authorization-refresh", "Renewed refresh token. It is renewed and delivered only when the expiration date is less than 7 days.", true)
                    )
                    .build()
            )
        )
        jwtService.deleteRefreshTokenByEmail(email)
    }

}