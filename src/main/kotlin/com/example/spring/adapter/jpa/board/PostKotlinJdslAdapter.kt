package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.PostJpaMapper
import com.example.spring.adapter.jpa.board.repository.PostKotlinJdslRepository
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PostKotlinJdslAdapter(
    private val postKotlinJdslRepository: PostKotlinJdslRepository,
    private val postJpaMapper: PostJpaMapper
) : PostKotlinJdslPort {
    override fun readPost(board: Board, postId: Int): Post? =
        postKotlinJdslRepository.readPost(board, postId)?.let {
            postJpaMapper.toPost(it)
        }

    override fun readPostPageList(boardId: Int, keyword: String?, searchType: String?, pageable: Pageable): Page<Post> {
        return postKotlinJdslRepository.readPostPageList(boardId, keyword, searchType, pageable).map {
            postJpaMapper.toPost(it)
        }
    }
}