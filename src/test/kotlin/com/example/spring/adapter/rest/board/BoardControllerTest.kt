package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.*
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.config.BoardDataNotFoundException
import com.example.spring.domain.board.Board
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class BoardControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var boardJpaPort: BoardJpaPort

    @Autowired
    private lateinit var memberJpaPort: MemberJpaPort

//    @Test
//    fun createBoard() {
//        val token = jwtService.createAccessToken("test")
//        val input = mutableMapOf<String, String>()
//        input["name"] = "test"
//        input["description"] = "test"
//        input["writer"] = "test"
//
//        val responseFields = arrayOf(
//            PayloadDocumentation.fieldWithPath("boardId").type(JsonFieldType.NUMBER)
//                .description("Unique board ID"),
//            PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
//                .description("Name of board"),
//            PayloadDocumentation.fieldWithPath("description")
//                .type(JsonFieldType.STRING)
//                .description("Description of board"),
//            PayloadDocumentation.fieldWithPath("writer").type(JsonFieldType.STRING)
//                .description("Writer of board"),
//            PayloadDocumentation.fieldWithPath("createdAt")
//                .type(JsonFieldType.STRING).description("Created datetime of board"),
//            PayloadDocumentation.fieldWithPath("modifier").type(JsonFieldType.STRING)
//                .optional().description("Modifier of board"),
//            PayloadDocumentation.fieldWithPath("updatedAt").type(JsonFieldType.STRING)
//                .optional().description("Updated datetime of board")
//        ).toList()
//
//        val requestFields = arrayOf(
//            PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
//                .description("Name of board"),
//            PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
//                .description("Description of board"),
//            PayloadDocumentation.fieldWithPath("writer").type(JsonFieldType.STRING)
//                .description("Writer of board"),
//        ).toList()
//
//        val responseHeaders = arrayOf(
//            HeaderDocumentation.headerWithName(HttpHeaders.LOCATION).description("Location header")
//        ).toList()
//
//        //when
//        var result = mockMvc.perform(
//            RestDocumentationRequestBuilders.post("/boards")
//                .header("Authorization", "Bearer $token")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectMapper().writeValueAsString(input))
//        )
//
//        //then
//        result
//            .andExpect(MockMvcResultMatchers.status().isCreated)
//            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("name").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("description").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("modifier").value(null))
//            .andExpect(MockMvcResultMatchers.jsonPath("updatedAt").value(null))
//            .andDo( // Rest Doc
//                MockMvcRestDocumentation.document(
//                    "POST-boards",
//                    HeaderDocumentation.responseHeaders(
//                        responseHeaders
//                    ),
//                    PayloadDocumentation.requestFields(
//                        requestFields
//                    ),
//                    PayloadDocumentation.responseFields(
//                        responseFields
//                    )
//                )
//            )
//            .andDo( // Swagger
//                MockMvcRestDocumentationWrapper.document(
//                    "POST-boards",
//                    Preprocessors.preprocessRequest(prettyPrint()),
//                    Preprocessors.preprocessResponse(prettyPrint()),
//                    snippets = arrayOf(
//                        ResourceDocumentation.resource(
//                            ResourceSnippetParameters.builder()
//                                .summary("Create board")
//                                .description("Create board with send info")
//                                .requestSchema(Schema("Request.boardCreate"))
//                                .responseSchema(Schema("Response.board"))
//                                .requestFields(
//                                    requestFields
//                                )
//                                .responseHeaders(
//                                    headerWithName(HttpHeaders.LOCATION).description("Location header")
//                                )
//                                .responseFields(
//                                    responseFields
//                                )
//                                .build()
//                        )
//                    )
//                )
//            )
//    }
//
//    @Test
//    fun `GET boards`() {
//        val token = jwtService.createAccessToken("test")
//
//        //when
//        var result = mockMvc.perform(
//            RestDocumentationRequestBuilders.get("/boards")
//                .header("Authorization", "Bearer $token")
//                .param("page", "0")
//                .param("size", "20")
//                .param("sort", "boardId,DESC")
//                .contentType(MediaType.APPLICATION_JSON)
//        )
//
//        val queryParameters = arrayOf(
//            ResourceDocumentation.parameterWithName("page").type(SimpleType.INTEGER)
//                .description("Requested page number(start is 0)"),
//            ResourceDocumentation.parameterWithName("size").type(SimpleType.INTEGER)
//                .description("The number of posts displayed on one page"),
//            ResourceDocumentation.parameterWithName("sort").type(SimpleType.INTEGER)
//                .description("Sort by request")
//        ).toList()
//
//        val responseFields = arrayOf(
//            PayloadDocumentation.fieldWithPath("currentPage")
//                .type(JsonFieldType.NUMBER).description("Number of current page"),
//            PayloadDocumentation.fieldWithPath("totalPages")
//                .type(JsonFieldType.NUMBER).description("Total count of page"),
//            PayloadDocumentation.fieldWithPath("totalElements")
//                .type(JsonFieldType.NUMBER).description("Total count of board"),
//            PayloadDocumentation.fieldWithPath("size")
//                .type(JsonFieldType.NUMBER)
//                .description("The number of posts displayed on one page"),
//            PayloadDocumentation.fieldWithPath("content[]").type(JsonFieldType.ARRAY).optional()
//                .description("Board List"),
//            PayloadDocumentation.fieldWithPath("content[].boardId").type(JsonFieldType.NUMBER)
//                .description("Unique board ID"),
//            PayloadDocumentation.fieldWithPath("content[].name").type(JsonFieldType.STRING)
//                .description("Name of board"),
//            PayloadDocumentation.fieldWithPath("content[].description")
//                .type(JsonFieldType.STRING)
//                .description("Description of board"),
//            PayloadDocumentation.fieldWithPath("content[].writer").type(JsonFieldType.STRING)
//                .description("Writer of board"),
//            PayloadDocumentation.fieldWithPath("content[].createdAt")
//                .type(JsonFieldType.STRING).description("Created datetime of board"),
//            PayloadDocumentation.fieldWithPath("content[].modifier").type(JsonFieldType.STRING)
//                .optional().description("Modifier of board"),
//            PayloadDocumentation.fieldWithPath("content[].updatedAt").type(JsonFieldType.STRING)
//                .optional().description("Updated datetime of board"),
//        ).toList()
//
//        //then
//        result
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("content").isArray)
//            .andExpect(MockMvcResultMatchers.jsonPath("currentPage").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("totalPages").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("totalElements").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("size").exists())
//            .andDo( // Rest Doc
//                MockMvcRestDocumentation.document(
//                    "POST-boards",
//                    RequestDocumentation.queryParameters(
//                        RequestDocumentation.parameterWithName("page").description("Requested page number(start is 0)"),
//                        RequestDocumentation.parameterWithName("page").description("Requested page number(start is 0)"),
//                        RequestDocumentation.parameterWithName("size")
//                            .description("The number of posts displayed on one page"),
//                        RequestDocumentation.parameterWithName("sort").description("Sort by request")
//                    ),
//                    PayloadDocumentation.responseFields(
//                        responseFields
//                    )
//                )
//            )
//            .andDo(
//                MockMvcRestDocumentationWrapper.document(
//                    "GET-boards",
//                    Preprocessors.preprocessRequest(prettyPrint()),
//                    Preprocessors.preprocessResponse(prettyPrint()),
//                    snippets = arrayOf(
//                        ResourceDocumentation.resource(
//                            ResourceSnippetParameters.builder()
//                                .summary("Read paging board list")
//                                .description(
//                                    """
//                                    Read paging board list
//                                """.trimIndent()
//                                )
//                                .queryParameters(
//                                    queryParameters
//                                )
//                                .responseSchema(Schema("Response.boardList"))
//                                .responseFields(
//                                    responseFields
//                                )
//                                .build()
//                        )
//                    )
//                )
//            )
//    }
//
//    @Test
//    fun `GET board-{boardId}`() {
//        val token = jwtService.createAccessToken("test")
//        val boardId = 1
//
//        val responseFields = arrayOf(
//            PayloadDocumentation.fieldWithPath("boardId").type(JsonFieldType.NUMBER)
//                .description("Unique board ID"),
//            PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
//                .description("Name of board"),
//            PayloadDocumentation.fieldWithPath("description")
//                .type(JsonFieldType.STRING)
//                .description("Description of board"),
//            PayloadDocumentation.fieldWithPath("writer").type(JsonFieldType.STRING)
//                .description("Writer of board"),
//            PayloadDocumentation.fieldWithPath("createdAt")
//                .type(JsonFieldType.STRING).description("Created datetime of board"),
//            PayloadDocumentation.fieldWithPath("modifier").type(JsonFieldType.STRING)
//                .optional().description("Modifier of board"),
//            PayloadDocumentation.fieldWithPath("updatedAt").type(JsonFieldType.STRING)
//                .optional().description("Updated datetime of board")
//        ).toList()
//
//        //when
//        val result = mockMvc.perform(
//            RestDocumentationRequestBuilders.get("/boards/{boardId}", boardId)
//                .header("Authorization", "Bearer $token")
//                .contentType(MediaType.APPLICATION_JSON)
//        )
//
//        //then
//        result
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("name").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("description").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("modifier")
//                    .value(Matchers.anyOf(Matchers.instanceOf(String::class.java), Matchers.nullValue()))
//            )
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("updatedAt")
//                    .value(Matchers.anyOf(Matchers.instanceOf(String::class.java), Matchers.nullValue()))
//            )
//            .andDo( // Rest Doc
//                MockMvcRestDocumentation.document(
//                    "GET-boards-{boardId}",
//                    PayloadDocumentation.responseFields(
//                        responseFields
//                    )
//                )
//            )
//            .andDo( // Swagger
//                MockMvcRestDocumentationWrapper.document(
//                    "GET-boards-{boardId}",
//                    Preprocessors.preprocessRequest(prettyPrint()),
//                    Preprocessors.preprocessResponse(prettyPrint()),
//                    snippets = arrayOf(
//                        ResourceDocumentation.resource(
//                            ResourceSnippetParameters.builder()
//                                .summary("Read board detail")
//                                .description("Read board just one record detail")
//                                .responseSchema(Schema("Response.board"))
//                                .pathParameters(
//                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER)
//                                        .description("Unique board ID")
//                                )
//                                .responseFields(
//                                    responseFields
//                                )
//                                .build()
//                        )
//                    )
//                )
//            )
//    }
//
//    @Test
//    fun `PATCH boards-{boardId}`() {
//        val token = jwtService.createAccessToken("test")
//        val input = mutableMapOf<String, String>()
//        input["name"] = "test"
//        input["description"] = "test"
//        input["modifier"] = "test"
//        val boardId = 1
//
//        val responseFields = arrayOf(
//            PayloadDocumentation.fieldWithPath("boardId").type(JsonFieldType.NUMBER)
//                .description("Unique board ID"),
//            PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
//                .description("Name of board"),
//            PayloadDocumentation.fieldWithPath("description")
//                .type(JsonFieldType.STRING)
//                .description("Description of board"),
//            PayloadDocumentation.fieldWithPath("writer").type(JsonFieldType.STRING)
//                .description("Writer of board"),
//            PayloadDocumentation.fieldWithPath("createdAt")
//                .type(JsonFieldType.STRING).description("Created datetime of board"),
//            PayloadDocumentation.fieldWithPath("modifier").type(JsonFieldType.STRING)
//                .optional().description("Modifier of board"),
//            PayloadDocumentation.fieldWithPath("updatedAt").type(JsonFieldType.STRING)
//                .optional().description("Updated datetime of board")
//        ).toList()
//
//        val requestFields = arrayOf(
//            PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
//                .description("Name of board"),
//            PayloadDocumentation.fieldWithPath("description")
//                .type(JsonFieldType.STRING)
//                .description("Description of board"),
//            PayloadDocumentation.fieldWithPath("modifier").type(JsonFieldType.STRING)
//                .optional().description("Modifier of board")
//        ).toList()
//
//        //when
//        var result = mockMvc.perform(
//            RestDocumentationRequestBuilders.patch("/boards/{boardId}", boardId)
//                .header("Authorization", "Bearer $token")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(ObjectMapper().writeValueAsString(input))
//        )
//
//        //then
//        result
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("name").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("description").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("modifier")
//                    .value(Matchers.anyOf(Matchers.instanceOf(String::class.java), Matchers.nullValue()))
//            )
//            .andExpect(
//                MockMvcResultMatchers.jsonPath("updatedAt")
//                    .value(Matchers.anyOf(Matchers.instanceOf(String::class.java), Matchers.nullValue()))
//            )
//            .andDo( // Rest Doc
//                MockMvcRestDocumentation.document(
//                    "PATCH-boards-{boardId}",
//                    RequestDocumentation.pathParameters(
//                        RequestDocumentation.parameterWithName("boardId").description("Unique board ID")
//                    ),
//                    PayloadDocumentation.requestFields(
//                        requestFields
//                    ),
//                    PayloadDocumentation.responseFields(
//                        responseFields
//                    )
//                )
//            )
//            .andDo( // Swagger
//                MockMvcRestDocumentationWrapper.document(
//                    "PATCH-boards-{boardId}",
//                    Preprocessors.preprocessRequest(prettyPrint()),
//                    Preprocessors.preprocessResponse(prettyPrint()),
//                    snippets = arrayOf(
//                        ResourceDocumentation.resource(
//                            ResourceSnippetParameters.builder()
//                                .summary("Update board detail")
//                                .description("Update board detail")
//                                .requestSchema(Schema("Request.boardUpdate"))
//                                .requestFields(
//                                    requestFields
//                                )
//                                .responseSchema(Schema("Response.board"))
//                                .pathParameters(
//                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER)
//                                        .description("Unique board ID")
//                                )
//                                .responseFields(
//                                    responseFields
//                                )
//                                .build()
//                        )
//                    )
//                )
//            )
//
//        Assertions.assertTrue {
//            boardJpaPort.readBoard(boardId).boardId == boardId
//        }
//    }
//
//    @Test
//    fun `DELETE board-{boardId}`() {
//        val token = jwtService.createAccessToken("test")
//        val board = Board(
//            name = "test",
//            description = "test",
//            writer = memberJpaPort.findMemberByEmail("test")
//        )
//        val boardId = boardJpaPort.createBoard(board).boardId
//
//        //when
//        var result = mockMvc.perform(
//            RestDocumentationRequestBuilders.delete("/boards/{boardId}", boardId)
//                .header("Authorization", "Bearer $token")
//                .contentType(MediaType.APPLICATION_JSON)
//        )
//
//        //then
//        result
//            .andExpect(MockMvcResultMatchers.status().isNoContent)
//            .andDo( // Rest Doc
//                MockMvcRestDocumentation.document(
//                    "PATCH-boards-{boardId}",
//                    RequestDocumentation.pathParameters(
//                        RequestDocumentation.parameterWithName("boardId").description("Unique board ID")
//                    )
//                )
//            )
//            .andDo( // Swagger
//                MockMvcRestDocumentationWrapper.document(
//                    "DELETE-boards-{boardId}",
//                    Preprocessors.preprocessRequest(prettyPrint()),
//                    Preprocessors.preprocessResponse(prettyPrint()),
//                    snippets = arrayOf(
//                        ResourceDocumentation.resource(
//                            ResourceSnippetParameters.builder()
//                                .summary("Delete board success")
//                                .description("Delete Board with BoardId")
//                                .pathParameters(
//                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER)
//                                        .description("Unique board ID")
//                                )
//                                .build()
//                        )
//                    )
//                )
//            )
//
//        Assertions.assertThrows(BoardDataNotFoundException::class.java) {
//            boardJpaPort.readBoard(boardId)
//        }
//    }
}