package com.example.spring.adapter.rest.board.dto

import com.example.spring.config.common.PaginationRequest

class BoardReadBoardListRequest : PaginationRequest() {
    var keyword: String? = null // 검색 키워드
    var searchType: String? = null // 검색 유형
    var orderBy: String = "boardId" // 정렬 유형
}