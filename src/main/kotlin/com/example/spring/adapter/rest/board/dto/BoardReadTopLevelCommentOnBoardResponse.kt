package com.example.spring.adapter.rest.board.dto

import com.example.spring.domain.board.Comment

data class BoardReadTopLevelCommentOnBoardResponse(
    val isEmpty: Boolean,
    val isLast: Boolean,
    val totalCount: Int,
    val pageNumber: Int,
    val commentList: List<Comment>
)
