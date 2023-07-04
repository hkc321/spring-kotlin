package com.example.spring.application.port.`in`.board

import com.example.spring.domain.board.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BoardUseCase {
    /**
     * 게시판 생성
     * */
    fun createBoard(commend: Commend.CreateCommend): Board

    /**
     * 게시판 리스트 조회
     * */
    fun readBoardPageList(commend: Commend.ReadListCommend): Page<Board>

    /**
     * 게시판 읽기
     * */
    fun readBoard(commend: Commend.ReadCommend): Board

    /**
     * 게시판 수정
     * */
    fun updateBoard(commend: Commend.UpdateCommend): Board

    /**
     * 게시판 삭제
     * */
    fun deleteBoard(commend: Commend.DeleteCommend)

    sealed class Commend {
        data class CreateCommend(
            val name: String,
            val description: String,
            val writer: String
        ) : Commend()

        data class ReadListCommend(
            var keyword: String? = null, // 검색 키워드
            var searchType: String? = null, // 검색 유형
            var pageable: Pageable
        ) : Commend()

        data class ReadCommend(
            val boardId: Int
        ) : Commend()

        data class UpdateCommend(
            val boardId: Int,
            val name: String,
            val description: String,
            val modifier: String
        ) : Commend()

        data class DeleteCommend(
            val boardId: Int
        ) : Commend()

    }
}