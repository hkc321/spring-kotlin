package com.example.spring.application.port.out.board

interface PostRedisPort {
    /**
     * 게시글 좋아요 추가
     * */
    fun createPostLike(boardId: Int, postId: Int, email: String): Int?

    /**
     * 게시글 좋아요 수
     * */
    fun countPostLike(boardId: Int, postId: Int): Int

    /**
     * 게시글 좋아요 눌렀는지 판단
     * */
    fun checkPostLikeByEmail(boardId: Int, postId: Int, email: String): Boolean

    /**
     * 게시글 좋아요 제거
     * */
    fun deletePostLike(boardId: Int, postId: Int, email: String): Int?

    /**
     * 게시글 좋아요 모두 제거
     * */
    fun deletePostLikeAll(boardId: Int, postId: Int)
}