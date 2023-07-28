package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.application.port.out.board.PostRedisPort
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

    @Autowired
    private lateinit var postRedisPort: PostRedisPort


    @Test
    @Transactional
    fun createPost() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val input = mutableMapOf<String, String>()
        input["title"] = "testqqq"
        input["content"] = email
        val boardId = 2

        //when
        val result = mockMvc.perform(
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
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").value(null),
        ).andDocument(
            "POST-boards-{boardId}-posts",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Create post")
                    .description("게시글을 생성합니다.")
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
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of post", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like post or not", false),
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
        postRedisPort.deletePostLikeAll(fromBoard.boardId, createdPostId)
        Assertions.assertThrows(PostDataNotFoundException::class.java) {
            postJpaPort.readPost(fromBoard, createdPostId)
        }
    }

    @Test
    fun readPostPageList() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/boards/{boardId}/posts", boardId)
                .header("Authorization", "Bearer $token")
                .queryParam("page", "1")
                .queryParam("size", "20")
                .queryParam("sort", "postId")
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
                    .summary("Read post paging list")
                    .description("게시글 목록을 조회합니다. 페이징 처리가 된 형태로 반환되기 때문에 요청할 페이지, 한 페이지의 보여줄 게시글의 갯수, 정렬 기준을 전송해야 합니다.")
                    .tag("posts")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false)
                    )
                    .queryParameters(
                        parameter("searchType", SimpleType.STRING, "검색 조건. (Only title, content or write)", true),
                        parameter("keyword", SimpleType.STRING, "검색 키워드", true),
                        parameter(
                            "page",
                            SimpleType.INTEGER,
                            "요청할 페이지 넘버(page > 0)",
                            false
                        ),
                        parameter(
                            "size",
                            SimpleType.INTEGER,
                            "한 페이지에 보여줄 게시글 갯수. min = 1, max = 50",
                            false
                        ),
                        parameter("sort", SimpleType.STRING, "정렬기준(only postId or like). 최근생성 순 혹은 좋아요 갯수 순으로 내림차순 정렬됩니다.", false)
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
                        field("content[].like", JsonFieldType.NUMBER, "Like(who click like) of post", false),
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
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2
        val postId = 2

        //when
        val result = mockMvc.perform(
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
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
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
                    .description("게시글 정보를 조회합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                    )
                    .responseSchema(Schema("postRead.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of post", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like post or not", false),
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
    fun updatePost() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val input = mutableMapOf<String, String>()
        input["title"] = "testqqq"
        input["content"] = email
        val boardId = 2
        val postId = 2

        //when
        val result = mockMvc.perform(
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
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists(),
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Update post")
                    .description("게시글을 업데이트 합니다. 작성자만 업데이트가 가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .requestSchema(Schema("postUpdate.Request"))
                    .requestFields(
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false)
                    )
                    .responseSchema(Schema("postUpdate.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of post", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like post or not", false),
                        field("writer", JsonFieldType.STRING, "Writer of post", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of post", false)
                    )
                    .build()
            )
        )
    }

    @Test
    @Transactional
    fun updatePostLike() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2
        val postId = 2

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/boards/{boardId}/posts/{postId}/like", boardId, postId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("postId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("title").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists(),
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}-like",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Update post like")
                    .description("게시글의 좋아요를 추가합니다. 이미 좋아요를 클릭한 게시글은 중복이 불가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .responseSchema(Schema("postUpdateLike.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of post", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like post or not", false),
                        field("writer", JsonFieldType.STRING, "Writer of post", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of post", false)
                    )
                    .build()
            )
        )

        postRedisPort.deletePostLike(boardId, postId, email)
        Assertions.assertFalse { postRedisPort.checkPostLikeByEmail(boardId, postId, email) }
    }

    @Test
    @Transactional
    fun deletePostLike() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2
        val postId = 2

        postRedisPort.createPostLike(boardId, postId, email)

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/boards/{boardId}/posts/{postId}/unlike", boardId, postId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("postId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("title").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists(),
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}-unlike",
            snippets = makeSnippets(
                ResourceSnippetParameters.builder()
                    .tag("posts")
                    .summary("Update post unlike")
                    .description("게시글의 좋아요를 제거합니다. 좋아요를 클릭한 이력이 있어야만 가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .responseSchema(Schema("postUpdateUnLike.Response"))
                    .responseFields(
                        field("postId", JsonFieldType.NUMBER, "Unique post ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of post", false),
                        field("title", JsonFieldType.STRING, "Title of post", false),
                        field("content", JsonFieldType.STRING, "Content of post", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of post", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like post or not", false),
                        field("writer", JsonFieldType.STRING, "Writer of post", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of post", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of post", false)
                    )
                    .build()
            )
        )
        Assertions.assertFalse { postRedisPort.checkPostLikeByEmail(boardId, postId, email) }
    }

    @Test
    @Transactional
    fun deletePost() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2
        val post = Post(
            boardId = boardId,
            title = "testTitle",
            content = "testContent",
            writer = email
        )
        val postId = postJpaPort.createPost(post).postId

        //when
        val result = mockMvc.perform(
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
                    .description("게시글을 제거합니다. 작성자만 제거 가능합니다.")
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