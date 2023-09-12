package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Comment

interface CommentKotlinJdslPort {
    /**
     * 최상위 레벨 댓글 조회
     * */
    fun readTopLevelComment(boardId: Int, postId: Int, size: Int, cursor: Int?, orderBy: String): Pair<List<Comment>, Int?>

    /**
     * 대댓글 조회
     * */
    fun readChildComment(boardId: Int, postId: Int, parentCommentId: Int, size: Int, cursor: Int?): Pair<List<Comment>, Int?>

    /**
     * 댓글 여러개 삭제
     * post 삭제 시 작동
     * */
    fun deleteAllComment(boardId: Int, postId: Int)
}