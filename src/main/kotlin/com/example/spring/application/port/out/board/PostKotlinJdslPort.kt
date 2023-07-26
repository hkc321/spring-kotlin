package com.example.spring.application.port.out.board

import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostKotlinJdslPort {
    /**
     * 게시글 조회
     * */
    fun readPost(board: Board, postId: Int): Post?

    /**
     * 게시글 리스트 조회
     * */
    fun readPostPageList(boardId: Int, keyword: String?, searchType: String?, pageable: Pageable): Page<Post>
}