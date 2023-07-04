package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post

interface PostJpaPort {
    /**
     * 게시글 생성
     * */
    fun createPost(post: Post): Post

    /**
     * 게시글 조회
     * */
    fun readPost(board: Board, postId: Int): Post

    /**
     * 게시글 수정
     * */
    fun updatePost(post: Post): Post

    /**
     * 게시글 삭제
     * */
    fun deletePost(board: Board, postId: Int)
}