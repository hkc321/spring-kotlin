package com.example.spring.application.port.`in`.board

import com.example.spring.domain.board.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostUseCase {
    /**
     * 게시글 생성
     * */
    fun createPost(commend: Commend.CreateCommend): Post

    /**
     * 게시글 리스트 조회
     * */
    fun readPostPageList(commend: Commend.ReadListCommend): Page<Post>

    /**
     * 게시글 조회
     * */
    fun readPost(commend: Commend.ReadCommend): Post

    /**
     * 게시글 수정
     * */
    fun updatePost(commend: Commend.UpdateCommend): Post

    /**
     * 게시글 좋아요
     * */
    fun likePost(commend: Commend.LikeCommend): Post

    /**
     * 게시글 좋아요 취소
     * */
    fun deleteLikePost(commend: Commend.LikeCommend): Post

    /**
     * 게시글 삭제
     * */
    fun deletePost(commend: Commend.DeleteCommend)

    sealed class Commend {
        abstract val boardId: Int

        data class CreateCommend(
            override val boardId: Int,
            val title: String,
            val content: String,
            val writer: String
        ) : Commend()

        data class ReadListCommend(
            override val boardId: Int,
            var keyword: String? = null, // 검색 키워드
            var searchType: String? = null, // 검색 유형
            var pageable: Pageable
        ) : Commend()

        data class ReadCommend(
            override val boardId: Int,
            val postId: Int,
            val reader: String = ""
        ) : Commend()

        data class UpdateCommend(
            override val boardId: Int,
            val postId: Int,
            val title: String,
            val content: String,
            val modifier: String
        ) : Commend()

        data class LikeCommend(
            override val boardId: Int,
            val postId: Int,
            val email: String
        ) : Commend()

        data class DeleteCommend(
            override val boardId: Int,
            val postId: Int,
            val modifier: String,
        ) : Commend()

    }
}