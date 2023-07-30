package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.application.port.out.board.CommentRedisPort
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.board.CommentService
import com.example.spring.application.service.board.exception.CommentDataNotFoundException
import com.example.spring.application.service.member.JwtService
import com.example.spring.domain.board.Comment
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
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
class CommentControllerDocsTest : RestdocsTestDsl {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var commentJpaPort: CommentJpaPort

    @Autowired
    private lateinit var memberJpaPort: MemberJpaPort

    @Autowired
    private lateinit var commentRedisPort: CommentRedisPort

    @Autowired
    private lateinit var commentService: CommentService

    @Test
    @Transactional
    fun createComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val input = mutableMapOf<String, Any?>()
        input["parentCommentId"] = null
        input["level"] = 1
        input["content"] = "testContent"
        val boardId = 2
        val postId = 2

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isCreated,
            MockMvcResultMatchers.jsonPath("commentId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("postId").value(postId),
            MockMvcResultMatchers.jsonPath("parentCommentId").value(nullOrInt()),
            MockMvcResultMatchers.jsonPath("level").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("childCommentCount").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").value(null)
        ).andDocument(
            "POST-boards-{boardId}-posts-{postId}-comments",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Create comment")
                    .description("댓글을 생성합니다. 대댓글만 지원하기 때문에 level은 1 또는 2만 가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .requestSchema(Schema("commentCreate.Request"))
                    .requestFields(
                        field(
                            "parentCommentId",
                            JsonFieldType.STRING,
                            "To comment on an existing comment, enter the commentId of the comment.",
                            true
                        ),
                        field(
                            "level",
                            JsonFieldType.NUMBER,
                            "Must be 1 or 2. To comment on an existing comment, enter the level (parentComment's level + 1)",
                            true
                        ),
                        field("content", JsonFieldType.STRING, "Content of comment")
                    )
                    .responseHeaders(
                        header(HttpHeaders.LOCATION, "Location header")
                    )
                    .responseSchema(Schema("commentCreate.Response"))
                    .responseFields(
                        field("commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("content", JsonFieldType.STRING, "Content of comment", false),
                        field("writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of comment", true)
                    )
                    .build()
            )
        )

        val jsonNode = ObjectMapper().readTree(result.andReturn().response.contentAsString)
        val commentId = jsonNode["commentId"].asInt()
        val commentBoardId = jsonNode["boardId"].asInt()
        val commentPostId = jsonNode["postId"].asInt()


        commentJpaPort.deleteComment(commentBoardId, commentPostId, commentId)
        commentRedisPort.deleteCommentLikeAll(commentBoardId, commentPostId, commentId)
        assertThrows(CommentDataNotFoundException::class.java) {
            commentService.readComment(CommentUseCase.Commend.ReadCommend(commentBoardId, commentPostId, commentId, "test"))
        }
    }

    @Test
    fun readTopLevelComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val boardId = 2
        val postId = 2

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                .header("Authorization", "Bearer $token")
                .queryParam("size", "20")
                .queryParam("cursor", null)
                .queryParam("orderBy", "recent")
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            json("comments").isArray,
            json("nextCursor").value(nullOrInt())
        ).andDocument(
            "GET-boards-{boardId}-posts-{postId}-comments",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Read comments list")
                    .description("게시글의 최상위 레벨의 댓글을 조회합니다. 댓글은 no offset 방식으로 한 페이지에 보여줄 댓글의 갯수와 커서, 정렬조건을 넘겨야 합니다. 커서는 commentId 입니다. 커서가 없을 경우 처음부터 탐색합니다. 만약 존재하지 않는 커서를 전송할 경우 빈 리스트가 전달됩니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .queryParameters(
                        parameter(
                            "size",
                            SimpleType.INTEGER,
                            "한 페이지에 보여줄 댓글 갯수. min = 1, max = 50",
                            false
                        ),
                        parameter(
                            "cursor",
                            SimpleType.INTEGER,
                            "Cursor는 검색할 기준값이며, 커서 이후 부터 검색합니다. 커서값이 비어있으면 처음부터 검색합니다. DB에 없는 값을 커서로 넘기면 빈 리스트를 반환합니다.",
                            true
                        ),
                        parameter(
                            "orderBy",
                            SimpleType.STRING,
                            "정렬기준(Only recent or like). 최근생성 순 혹은 좋아요 갯수 순으로 내림차순 정렬됩니다.",
                            false
                        )
                    )
                    .responseSchema(Schema("commentReadTopLevel.Response"))
                    .responseFields(
                        field("comments[]", JsonFieldType.ARRAY, "Comments list", true),
                        field("comments[].commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("comments[].boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("comments[].postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("comments[].parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("comments[].level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("comments[].content", JsonFieldType.STRING, "Content of comment", false),
                        field("comments[].like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("comments[].isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("comments[].childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("comments[].writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("comments[].createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("comments[].updatedAt", JsonFieldType.STRING, "Updated datetime of comment", true),
                        field("nextCursor", JsonFieldType.NUMBER, "Value to use when looking up the next list.", true),
                    )
                    .build()
            )
        )
    }

    @Test
    fun readComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val boardId = 2
        val postId = 2
        val commentId = 1

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                "/v1/boards/{boardId}/posts/{postId}/comments/{commentId}",
                boardId,
                postId,
                commentId
            )
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("commentId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("postId").value(postId),
            MockMvcResultMatchers.jsonPath("parentCommentId").value(nullOrInt()),
            MockMvcResultMatchers.jsonPath("level").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("childCommentCount").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").value(nullOrString())
        ).andDocument(
            "GET-boards-{boardId}-posts-{postId}-comments-{commentId}",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Read comment")
                    .description("댓글 정보를 조회합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .responseSchema(Schema("commentRead.Response"))
                    .responseFields(
                        field("commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("content", JsonFieldType.STRING, "Content of comment", false),
                        field("writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of comment", true)
                    )
                    .build()
            )
        )
    }

    @Test
    fun readChildComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val boardId = 2
        val postId = 2
        val commentId = 1

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                "/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/childComment",
                boardId,
                postId,
                commentId
            )
                .header("Authorization", "Bearer $token")
                .queryParam("size", "20")
                .queryParam("cursor", null)
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            json("comments").isArray,
            json("nextCursor").value(nullOrInt())
        ).andDocument(
            "GET-boards-{boardId}-posts-{postId}-comments-{commentId}-childComment",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Read childComments list")
                    .description("최상위 댓글의 하위 댓글을 조회합니다. 현재는 대댓글 까지만 지원합니다. 최근 작성순으로 오름차순 정렬됩니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .queryParameters(
                        parameter(
                            "size",
                            SimpleType.INTEGER,
                            "한 페이지에 보여줄 댓글 갯수. min = 1, max = 50",
                            false
                        ),
                        parameter(
                            "cursor",
                            SimpleType.INTEGER,
                            "Cursor는 검색할 기준값이며, 커서 이후 부터 검색합니다. 커서값이 비어있으면 처음부터 검색합니다. DB에 없는 값을 커서로 넘기면 빈 리스트를 반환합니다.",
                            true
                        )
                    )
                    .responseSchema(Schema("commentChild.Response"))
                    .responseFields(
                        field("comments[]", JsonFieldType.ARRAY, "Comments list", true),
                        field("comments[].commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("comments[].boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("comments[].postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("comments[].parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("comments[].level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("comments[].like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("comments[].isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("comments[].childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("comments[].content", JsonFieldType.STRING, "Content of comment", false),
                        field("comments[].writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("comments[].createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("comments[].updatedAt", JsonFieldType.STRING, "Updated datetime of comment", true),
                        field("nextCursor", JsonFieldType.NUMBER, "Value to use when looking up the next list.", true),
                    )
                    .build()
            )
        )
    }

    @Test
    @Transactional
    fun updateComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val input = mutableMapOf<String, Any?>()
        input["content"] = "testContent"
        val boardId = 2
        val postId = 2
        val commentId = 1

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch(
                "/v1/boards/{boardId}/posts/{postId}/comments/{commentId}",
                boardId,
                postId,
                commentId
            )
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(input))
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("commentId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("postId").value(postId),
            MockMvcResultMatchers.jsonPath("parentCommentId").value(nullOrInt()),
            MockMvcResultMatchers.jsonPath("level").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("childCommentCount").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists()
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}-comments-{commentId}",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Update comment")
                    .description("댓글을 업데이트 합니다. 작성자만 수정이 가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .requestSchema(Schema("commentUpdate.Request"))
                    .requestFields(
                        field("content", JsonFieldType.STRING, "Content of comment")
                    )
                    .responseSchema(Schema("commentUpdate.Response"))
                    .responseFields(
                        field("commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("content", JsonFieldType.STRING, "Content of comment", false),
                        field("writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of comment", false)
                    )
                    .build()
            )
        )
    }

    @Test
    @Transactional
    fun updateCommentLike() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2
        val postId = 2
        val commentId = 1

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch(
                "/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/like",
                boardId,
                postId,
                commentId
            )
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("commentId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("postId").value(postId),
            MockMvcResultMatchers.jsonPath("parentCommentId").value(nullOrInt()),
            MockMvcResultMatchers.jsonPath("level").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("childCommentCount").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists()
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}-comments-{commentId}-like",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Update comment like")
                    .description("댓글의 좋아요를 추가합니다. 이미 좋아요를 클릭한 댓글은 중복이 불가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .responseSchema(Schema("commentUpdateLike.Response"))
                    .responseFields(
                        field("commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("content", JsonFieldType.STRING, "Content of comment", false),
                        field("writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of comment", false)
                    )
                    .build()
            )
        )

        commentRedisPort.deleteCommentLike(boardId, postId, commentId, email)
        Assertions.assertFalse { commentRedisPort.checkCommentLikeByEmail(boardId, postId, commentId, email) }
    }

    @Test
    @Transactional
    fun updateCommentUnLike() {
        val email = "test"
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail(email)!!)
        val boardId = 2
        val postId = 2
        val commentId = 1

        commentRedisPort.createCommentLike(boardId, postId, commentId, email)

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch(
                "/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/unlike",
                boardId,
                postId,
                commentId
            )
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        result.andExpectAll(
            MockMvcResultMatchers.status().isOk,
            MockMvcResultMatchers.jsonPath("commentId").exists(),
            MockMvcResultMatchers.jsonPath("boardId").value(boardId),
            MockMvcResultMatchers.jsonPath("postId").value(postId),
            MockMvcResultMatchers.jsonPath("parentCommentId").value(nullOrInt()),
            MockMvcResultMatchers.jsonPath("level").exists(),
            MockMvcResultMatchers.jsonPath("like").exists(),
            MockMvcResultMatchers.jsonPath("isLiked").exists(),
            MockMvcResultMatchers.jsonPath("childCommentCount").exists(),
            MockMvcResultMatchers.jsonPath("content").exists(),
            MockMvcResultMatchers.jsonPath("writer").exists(),
            MockMvcResultMatchers.jsonPath("createdAt").exists(),
            MockMvcResultMatchers.jsonPath("updatedAt").exists()
        ).andDocument(
            "PATCH-boards-{boardId}-posts-{postId}-comments-{commentId}-unlike",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Update comment unLike")
                    .description("댓글의 좋아요를 제거합니다. 좋아요를 클릭한 이력이 있어야만 가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .responseSchema(Schema("commentUpdateUnLike.Response"))
                    .responseFields(
                        field("commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("like", JsonFieldType.NUMBER, "Like(who click like) of comment", false),
                        field("isLiked", JsonFieldType.BOOLEAN, "Can member like comment or not", false),
                        field("childCommentCount", JsonFieldType.NUMBER, "Number of childComments", false),
                        field("content", JsonFieldType.STRING, "Content of comment", false),
                        field("writer", JsonFieldType.STRING, "Writer of comment", false),
                        field("createdAt", JsonFieldType.STRING, "Created datetime of comment", false),
                        field("updatedAt", JsonFieldType.STRING, "Updated datetime of comment", false)
                    )
                    .build()
            )
        )
        Assertions.assertFalse { commentRedisPort.checkCommentLikeByEmail(boardId, postId, commentId, email) }
    }

    @Test
    @Transactional
    fun deleteComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test")!!)
        val boardId = 2
        val postId = 2
        val comment = commentJpaPort.createComment(
            Comment(
                boardId = boardId,
                postId = postId,
                parentComment = null,
                level = 0,
                content = "testContent",
                writer = "test"
            )
        )
        val commentId = comment.commentId

        //when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                "/v1/boards/{boardId}/posts/{postId}/comments/{commentId}",
                boardId,
                postId,
                commentId
            )
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpectAll(
            MockMvcResultMatchers.status().isNoContent,
        ).andDocument(
            "DELETE-boards-{boardId}-posts-{postId}-comments-{commentId}",
            snippets = makeSnippets(
                snippetsBuilder()
                    .tag("comments")
                    .summary("Delete comment")
                    .description("댓글을 제거합니다. 작성자만 제거 가능합니다.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .build()
            )
        )

        assertThrows(CommentDataNotFoundException::class.java) {
            commentService.readComment(CommentUseCase.Commend.ReadCommend(boardId, postId, commentId, "test"))
        }
    }
}