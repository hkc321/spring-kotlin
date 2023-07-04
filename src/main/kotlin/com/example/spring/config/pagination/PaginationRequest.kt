package com.example.spring.config.pagination

open class PaginationRequest {
    var page: Int = 1 // 현재 페이지 번호
    var recordSize: Int = 5 // 페이지당 출력할 데이터 개수
    var pageSize: Int = 10 // 화면 하단에 출력할 페이지 사이즈
}