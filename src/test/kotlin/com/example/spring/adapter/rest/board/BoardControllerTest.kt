package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.*
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.example.spring.adapter.rest.board.dto.BoardRequest
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.service.member.JwtService
import com.example.spring.config.NoDataException
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
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


    @Test
    fun `GET board success`() {
        val token = jwtService.createAccessToken("test")

        //when
        var noParamResult = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/board")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        //then
        noParamResult
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("pageList").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("paginationInfo").hasJsonPath())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "success_GET_board",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Read paging board list")
                                .description("""
                                    Read paging board list
                                """.trimIndent())
                                .responseSchema(Schema("Response.boardList"))
                                .responseFields(
                                    fieldWithPath("paginationInfo.totalRecordCount").type(JsonFieldType.NUMBER).description("Total count of board"),
                                    fieldWithPath("paginationInfo.totalPageCount").type(JsonFieldType.NUMBER).description("Total count of page"),
                                    fieldWithPath("paginationInfo.startPage").type(JsonFieldType.NUMBER).description("Number of start page"),
                                    fieldWithPath("paginationInfo.endPage").type(JsonFieldType.NUMBER).description("Number of end page"),
                                    fieldWithPath("paginationInfo.limitStart").type(JsonFieldType.NUMBER).description("Number of start search"),
                                    fieldWithPath("paginationInfo.existPrevPage").type(JsonFieldType.BOOLEAN).description("Prev page exist or not"),
                                    fieldWithPath("paginationInfo.existNextPage").type(JsonFieldType.BOOLEAN).description("Next page exist or not"),
                                    fieldWithPath("paginationInfo.currentPage").type(JsonFieldType.NUMBER).description("Number of current page"),
                                    fieldWithPath("pageList[].boardId").type(JsonFieldType.NUMBER).description("Unique board ID"),
                                    fieldWithPath("pageList[].title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("pageList[].content").type(JsonFieldType.STRING).description("Content of board"),
                                    fieldWithPath("pageList[].up").type(JsonFieldType.NUMBER).description("Count of who click like"),
                                    fieldWithPath("pageList[].writer").type(JsonFieldType.STRING).description("Writer of board"),
                                    fieldWithPath("pageList[].createdAt").type(JsonFieldType.STRING).description("Created datetime of board"),
                                    fieldWithPath("pageList[].updatedAt").type(JsonFieldType.VARIES).description("Updated datetime of board"),
                                )
                                .build()
                        )
                    )
                )
            )

    }

    @Test
    fun `GET board-{boardId} success`() {
        val token = jwtService.createAccessToken("test")

        //when
        val successBoardId = 5
        var successResult = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/board/{boardId}", successBoardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        //then
        successResult
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("title").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("content").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("up").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("updatedAt").exists())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "success_GET_board/{boardId}",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Read board detail")
                                .description("Read board just one record detail")
                                .responseSchema(Schema("Response.board"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .responseFields(
                                    fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("Unique board ID"),
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("Content of board"),
                                    fieldWithPath("up").type(JsonFieldType.NUMBER).description("Count of who click like"),
                                    fieldWithPath("writer").type(JsonFieldType.STRING).description("Writer of board"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("Created datetime of board"),
                                    fieldWithPath("updatedAt").type(JsonFieldType.VARIES).description("Updated datetime of board"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `GET board-{boardId} fail`() {
        val token = jwtService.createAccessToken("test")

        //when
        val wrongBoardId = 0
        var wrongResult = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/board/{boardId}", wrongBoardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        //then
        wrongResult
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("errorCode").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("errorMessage").exists())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "fail_wrongBoardId_GET_board/{boardId}",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .responseSchema(Schema("Response.error"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .responseFields(
                                    fieldWithPath("errorCode").type(JsonFieldType.STRING).description("Error code"),
                                    fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("Error message"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `POST board success`() {
        val token = jwtService.createAccessToken("test")
        val input = mutableMapOf<String, String>()
        input["title"] = "test"
        input["content"] = "test"
        input["writer"] = "test"

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/board")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("title").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("content").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("up").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("updatedAt").value(null))
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "success_POST_board",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Create board")
                                .description("Create board with send info")
                                .requestSchema(Schema("Request.boardCreate"))
                                .responseSchema(Schema("Response.board"))
                                .requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("Content of board"),
                                    fieldWithPath("writer").type(JsonFieldType.STRING).description("Writer of board"),
                                )
                                .responseFields(
                                    fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("Unique board ID"),
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("Content of board"),
                                    fieldWithPath("up").type(JsonFieldType.NUMBER).description("Count of who click like"),
                                    fieldWithPath("writer").type(JsonFieldType.STRING).description("Writer of board"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("Created datetime of board"),
                                    fieldWithPath("updatedAt").type(JsonFieldType.VARIES).description("Updated datetime of board"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `POST board fail`() {
        val token = jwtService.createAccessToken("test")
        val input = mutableMapOf<String, String>()

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/board")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("errorCode").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("errorMessage").exists())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "fail post board",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .responseSchema(Schema("Response.error"))
                                .responseFields(
                                    fieldWithPath("errorCode").type(JsonFieldType.STRING).description("Error code"),
                                    fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("Error message"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `PATCH board-{boardID} success`() {
        val token = jwtService.createAccessToken("test")
        val input = mutableMapOf<String, String>()
        input["title"] = "test"
        input["content"] = "test"
        val boardId = 5

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/board/{boardId}", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("boardId").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("title").value(input["title"]))
            .andExpect(MockMvcResultMatchers.jsonPath("content").value(input["content"]))
            .andExpect(MockMvcResultMatchers.jsonPath("up").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("createdAt").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("updatedAt").exists())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "success patch board",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Update board")
                                .description("Update board with send info")
                                .requestSchema(Schema("Request.boardUpdate"))
                                .responseSchema(Schema("Response.error"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("Content of board"),
                                )
                                .responseFields(
                                    fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("Unique board ID"),
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("Content of board"),
                                    fieldWithPath("up").type(JsonFieldType.NUMBER).description("Count of who click like"),
                                    fieldWithPath("writer").type(JsonFieldType.STRING).description("Writer of board"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("Created datetime of board"),
                                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("Updated datetime of board"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `PATCH board-{boardID} fail wrong boardId`() {
        val token = jwtService.createAccessToken("test")
        val input = mutableMapOf<String, String>()
        input["title"] = "test2"
        input["content"] = "test2"
        val boardId = 0

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/board/{boardId}", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("DATA_NOT_FOUND"))
            .andExpect(MockMvcResultMatchers.jsonPath("errorMessage").exists())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "fail - wrong boardId",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .requestSchema(Schema("Request.boardUpdate"))
                                .responseSchema(Schema("Response.error"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("Title of board"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("Content of board"),
                                )
                                .responseFields(
                                    fieldWithPath("errorCode").type(JsonFieldType.STRING).description("Error code"),
                                    fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("Error message"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `PATCH board-{boardID} fail wrong invalid parameter`() {
        val token = jwtService.createAccessToken("test")
        val input = mutableMapOf<String, String>()
        val boardId = 37

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/board/{boardId}", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("INVALID_PARAMETER"))
            .andExpect(MockMvcResultMatchers.jsonPath("errorMessage").exists())
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "fail - invalid parameter",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .responseSchema(Schema("Response.error"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .responseFields(
                                    fieldWithPath("errorCode").type(JsonFieldType.STRING).description("Error code"),
                                    fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("Error message"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `DELETE board-{boardId} success`() {
        val token = jwtService.createAccessToken("test")
        val boardId = boardJpaPort.saveBoard(BoardRequest("test", "test", "test").toDomain()).boardId

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/board/{boardId}", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "delete success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Delete board success")
                                .description("Delete Board with BoardId")
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .build()
                        )
                    )
                )
            )

        Assertions.assertThrows(NoDataException::class.java) {
            boardJpaPort.loadBoard(boardId)
        }
    }

    @Test
    fun `DELETE board-{boardId} fail`() {
        val token = jwtService.createAccessToken("test")
        val boardId = 0

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/board/{boardId}", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        //then
        result
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "delete fail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .responseSchema(Schema("Response.error"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .responseFields(
                                    fieldWithPath("errorCode").type(JsonFieldType.STRING).description("Error code"),
                                    fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("Error message"),
                                )
                                .build()
                        )
                    )
                )
            )
    }

    @Test
    fun `GET board-{boardId}-comment`() {
        val token = jwtService.createAccessToken("test")
        val boardId = 0

        //when
        var noParamResult = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/board/{boardId}/comment", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        //then
        noParamResult
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("isEmpty").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("commentList").isArray)
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "success board comment list",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Read top level comment list")
                                .description("""
                                    Read top level comment list with boardId
                                """.trimIndent())
                                .responseSchema(Schema("Response.commentList"))
                                .pathParameters(
                                    ResourceDocumentation.parameterWithName("boardId").type(SimpleType.INTEGER).description("Unique board ID")
                                )
                                .responseFields(
                                    fieldWithPath("isEmpty").type(JsonFieldType.BOOLEAN).description("page list is empty or not"),
                                    fieldWithPath("isLast").type(JsonFieldType.BOOLEAN).description("page is last or not"),
                                    fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("total comment count"),
                                    fieldWithPath("pageNumber").type(JsonFieldType.NUMBER).description("present page number"),
                                    fieldWithPath("commentList").type(JsonFieldType.ARRAY).description("list of comment"),
                                )
                                .build()
                        )
                    )
                )
            )
    }
}