package com.example.spring.application.port.out.board

import com.example.spring.adapter.rest.board.dto.CommentCommonResponse
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Comment
import com.example.spring.domain.board.Post

interface CommentKotlinJdslPort {
    /**
     * 최상위 레벨 댓글 조회
     * */
    fun readTopLevelComment(boardId: Int, postId: Int, size: Int, cursor: Int?, orderBy: String): Pair<List<Comment>, Int?>

    /**
     * 대댓글 조회
     * */
    fun readChildComment(boardId: Int, postId: Int, parentCommentId: Int, size: Int, cursor: Int?): Pair<List<Comment>, Int?>
}