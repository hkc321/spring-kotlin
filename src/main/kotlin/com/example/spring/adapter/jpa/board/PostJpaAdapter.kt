package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.mapper.PostJpaMapper
import com.example.spring.adapter.jpa.board.repository.PostJpaRepository
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.config.controller.PostDataNotFoundException
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.Post
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PostJpaAdapter(
    private val postJpaRepository: PostJpaRepository,
    private val postJpaMapper: PostJpaMapper
) : PostJpaPort {

    override fun createPost(post: Post): Post =
        postJpaMapper.toPost(postJpaRepository.save(postJpaMapper.toJpaEntity(post)))

    override fun readPost(board: Board, postId: Int): Post =
        postJpaRepository.findByIdOrNull(postId)
            ?.let {
                return postJpaMapper.toPost(it)
            } ?: throw PostDataNotFoundException(boardId = board.boardId, postId = postId)

    override fun updatePost(post: Post): Post =
        postJpaMapper.toPost(postJpaRepository.save(postJpaMapper.toJpaEntity(post)))

    override fun deletePost(board: Board, postId: Int) =
        postJpaRepository.deleteById(postId)
}