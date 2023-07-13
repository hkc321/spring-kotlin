package com.example.spring.adapter.rest.board

import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.application.port.out.member.MemberJpaPort
import com.example.spring.application.service.board.exception.CommentDataNotFoundException
import com.example.spring.application.service.member.JwtService
import com.example.spring.domain.board.Comment
import com.fasterxml.jackson.databind.ObjectMapper
import config.RestdocsTestDsl
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

    @Test
    fun createComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val input = mutableMapOf<String, Any?>()
        input["parentCommentId"] = null
        input["level"] = 0
        input["content"] = "testContent"
        val boardId = 2
        val postId = 2

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/boards/{boardId}/posts/{postId}/comments", boardId, postId)
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
            MockMvcResultMatchers.jsonPath("up").exists(),
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
                    .description("Create comment with send info")
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
                            "Must be 1 or higher. To comment on an existing comment, enter the level (parentComment's level + 1)",
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
                        field("up", JsonFieldType.NUMBER, "Up(who click like) of comment", false),
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
        assertThrows(CommentDataNotFoundException::class.java) {
            commentJpaPort.readComment(commentBoardId, commentPostId, commentId)
        }
    }

    @Test
    fun readTopLevelComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val boardId = 2
        val postId = 2

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/boards/{boardId}/posts/{postId}/comments", boardId, postId)
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
                    .description("Read topLevelComments list with send info.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false)
                    )
                    .queryParameters(
                        parameter(
                            "size",
                            SimpleType.INTEGER,
                            "The number of comment lists the user wants to receive. min = 1, max = 50",
                            false
                        ),
                        parameter("cursor", SimpleType.INTEGER, "Cursor is the reference value to search for, and searches after the cursor. If it is null, search from the beginning.", true),
                        parameter("orderBy", SimpleType.STRING, "Only up or recent are allowed as sorting criteria", false)
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
                        field("comments[].up", JsonFieldType.NUMBER, "Up(who click like) of comment", false),
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
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val boardId = 2
        val postId = 2
        val commentId = 5

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId)
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
            MockMvcResultMatchers.jsonPath("up").exists(),
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
                    .description("Read comment with send info")
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
                        field("up", JsonFieldType.NUMBER, "Up(who click like) of comment", false),
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
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val boardId = 2
        val postId = 2
        val commentId = 5

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/boards/{boardId}/posts/{postId}/comments/{commentId}/childComment", boardId, postId, commentId)
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
                    .description("Read childComments list with send info. This list is sorted in ascending order by newest only.")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .queryParameters(
                        parameter(
                            "size",
                            SimpleType.INTEGER,
                            "The number of comment lists the user wants to receive. min = 1, max = 50",
                            false
                        ),
                        parameter("cursor", SimpleType.INTEGER, "Cursor is the reference value to search for, and searches after the cursor. If it is null, search from the beginning.", true)
                    )
                    .responseSchema(Schema("commentChild.Response"))
                    .responseFields(
                        field("comments[]", JsonFieldType.ARRAY, "Comments list", true),
                        field("comments[].commentId", JsonFieldType.NUMBER, "Unique comment ID", false),
                        field("comments[].boardId", JsonFieldType.NUMBER, "BoardId of comment", false),
                        field("comments[].postId", JsonFieldType.NUMBER, "PostId of comment", false),
                        field("comments[].parentCommentId", JsonFieldType.NUMBER, "ParentCommentId of comment", true),
                        field("comments[].level", JsonFieldType.NUMBER, "Level of comment", false),
                        field("comments[].up", JsonFieldType.NUMBER, "Up(who click like) of comment", false),
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
    fun updateComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
        val input = mutableMapOf<String, Any?>()
        input["content"] = "testContent"
        val boardId = 2
        val postId = 2
        val commentId = 5

        //when
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.patch(
                "/boards/{boardId}/posts/{postId}/comments/{commentId}",
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
            MockMvcResultMatchers.jsonPath("up").exists(),
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
                    .description("Update comment with send info")
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
                        field("up", JsonFieldType.NUMBER, "Up(who click like) of comment", false),
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
    fun deleteComment() {
        val token = jwtService.createAccessToken(memberJpaPort.findMemberByEmail("test"))
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
        var result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                "/boards/{boardId}/posts/{postId}/comments/{commentId}",
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
                    .description("Delete comment with send info")
                    .pathParameters(
                        parameter("boardId", SimpleType.INTEGER, "Unique board ID", false),
                        parameter("postId", SimpleType.INTEGER, "Unique post ID", false),
                        parameter("commentId", SimpleType.INTEGER, "Unique comment ID", false)
                    )
                    .build()
            )
        )

        assertThrows(CommentDataNotFoundException::class.java) {
            commentJpaPort.readComment(boardId, postId, commentId)
        }
    }
}