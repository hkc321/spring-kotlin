package com.example.spring.application.port.out.board

interface CommentRedisPort {
    /**
     * 댓글 좋아요 추가
     * */
    fun createCommentLike(boardId: Int, postId: Int, commentId: Int, email: String): Int

    /**
     * 댓글 좋아요 수
     * */
    fun countCommentLike(boardId: Int, postId: Int, commentId: Int): Int

    /**
     * 댓글 좋아요 눌렀는지 판단
     * */
    fun checkCommentLikeByEmail(boardId: Int, postId: Int, commentId: Int, email: String): Boolean

    /**
     * 댓글 좋아요 제거
     * */
    fun deleteCommentLike(boardId: Int, postId: Int, commentId: Int, email: String): Int
}