package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.board.exception.PostDataNotFoundException
import com.example.spring.application.service.member.JwtService
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class PostControllerDocsTest : RestdocsTestDsl {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var postJpaPort: PostJpaPort

    @Autowired
    private lateinit var boardJpaPort: BoardJpaPort
    
    @Autowired
    private lateinit var memberJpaPort: MemberJpaPort


    @Test
    @Transactional
    fun createPost() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val input = mutableMapOf<String, String>()
        input["title"] = "testqqq"
        input["content"] = "test"
        val boardId = 2

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/boards/{boardId}/posts", boardId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isCreated,
            MockMvcResultMatchers.jsonPath("postId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("title").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("up").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").value(null),
        ).andDocument(
            "POST-boards-{boardId}-posts",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Create post")
                    .description("Create post with send info")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false)
                    )
                    .requestSchema(Schema("postCreate.Request"))
                    .requestFields(
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false)
                    )
                    .responseHeaders(
                        header(HttpHeaders.LOCATION, "Location header")
                    )
                    .responseSchema(Schema("postCreate.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("up", JsonFieldType.NUMBER, "Up(who click like) of post", false),
                        field("writer", JsonFieldType.STRING, "Writer of post", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of post", true)
                    )
                    .build()
            )
        )
        val jsonNode = ObjectMapper().readTree(result.andReturn().response.contentAsString)
        val createdPostId = jsonNode["postId"].asInt()
        val fromBoard: Board = boardJpaPort.readBoard(jsonNode["boardId"].asInt())

        postJpaPort.deletePost(fromBoard, createdPostId)
        Assertions.assertThrows(PostDataNotFoundException::class.java) {
            postJpaPort.readPost(fromBoard, createdPostId)
        }
    }

    @Test
    fun readPostPageList() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val boardId = 2

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/boards/{boardId}/posts", boardId)
                .header("Authorization", "Bearer $token")
                .queryParam("page", "0")
                .queryParam("size", "20")
                .queryParam("sort", "postId,DESC")
                .queryParam("keyword", null)
                .queryParam("searchType", null)
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("content").isArray,
            MockMvcResultMatchers.jsonPath("currentPage").exists(),
            MockMvcResultMatchers.jsonPath("totalPages").exists(),
            MockMvcResultMatchers.jsonPath("totalElements").exists(),
            MockMvcResultMatchers.jsonPath("size").exists()
        ).andDocument(
            "GET-boards-{boardId}-posts",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Read board paging list")
                    .description("Read post paging list with send info.")
                    .tag("posts")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false)
                    )
                    .queryParameters(
                        parameter("searchType", SimpleType.STRING, "Search requirement.", true),
                        parameter("keyword", SimpleType.STRING, "Search keyword", true),
                        parameter(
                            "page",
                            SimpleType.INTEGER,
                            "Requested page number(start is 0 not 1). And default is 0",
                            true
                        ),
                        parameter(
                            "size",
                            SimpleType.INTEGER,
                            "The number of posts displayed on one page. Default is 20",
                            true
                        ),
                        parameter("sort", SimpleType.STRING, "Sort by request. Default is [postId,DESC]", true)
                    )
                    .responseSchema(Schema("postReadPageList.Response"))
                    .responseFields(
                        field("currentPage", JsonFieldType.NUMBER, "Number of current page", false),
                        field("totalPages", JsonFieldType.NUMBER, "Total count of page", false),
                        field("totalElements", JsonFieldType.NUMBER, "Total count of post", false),
                        field("size", JsonFieldType.NUMBER, "The number of posts displayed on one page", false),
                        field("content[]", JsonFieldType.ARRAY, "Post list", true),
                        field("content[].postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("content[].boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("content[].title", JsonFieldType.STRING, "Title of post", false),
                        field("content[].content", JsonFieldType.STRING, "Content if post", false),
                        field("content[].up", JsonFieldType.NUMBER, "Up(who click like) of post", false),
                        field("content[].writer", JsonFieldType.STRING, "Writer of post", false),
                        field("content[]createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("content[]updatedAt", JsonFieldType.STRING, "Updated datetime of post", true)
                    )
                    .build()
            )
        )
    }

    @Test
    fun readPost() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val boardId = 2
        val postId = 2

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/boards/{boardId}/posts/{postId}", boardId, postId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("postId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("title").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("up").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt")
                .value(Matchers.anyOf(Matchers.instanceOf(String::class.java), Matchers.nullValue())),
        ).andDocument(
            "GET-boards-{boardId}-posts-{postId}",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Read post")
                    .description("Read post with send info")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "BoardId of post", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                    )
                    .responseSchema(Schema("postRead.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("up", JsonFieldType.NUMBER, "Up(who click like) of post", false),
                        field("writer", JsonFieldType.STRING, "Writer of post", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of post", true)
                    )
                    .build()
            )
        )
    }

    @Test
    fun updatePost() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val input = mutableMapOf<String, String>()
        input["title"] = "testqqq"
        input["content"] = "test"
        val boardId = 2
        val postId = 2

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/boards/{boardId}/posts/{postId}", boardId, postId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("postId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("title").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("up").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists(),
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Update post")
                    .description("Update post with send info")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .requestSchema(Schema("postUpdate.Request"))
                    .requestFields(
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false)
                    )
                    .responseSchema(Schema("postCreate.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("up", JsonFieldType.NUMBER, "Up(who click like) of post", false),
                        field("writer", JsonFieldType.STRING, "Writer of post", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of post", true)
                    )
                    .build()
            )
        )
    }

    @Test
    @Transactional
    fun deletePost() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val boardId = 2
        val post = Post(
            boardId = boardId,
            title = "testTitle",
            content = "testContent",
            writer = "test"
        )
        val postId = postJpaPort.createPost(post).postId

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/boards/{boardId}/posts/{postId}", boardId, postId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpectAll(
            MockMvcResultMatchers.status().isNoContent
        ).andDocument(
            "DELETE-boards-{boardId}-posts-{postId}",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Delete post")
                    .description("Delete post with send info")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .build()
            )
        )
        Assertions.assertThrows(PostDataNotFoundException::class.java) {
            postJpaPort.readPost(boardJpaPort.readBoard(boardId), postId)
        }
    }
}