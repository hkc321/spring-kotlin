package com.example.spring.config.common

class Pagination(
    totalRecordCount: Int,
    page: Int,
    recordSize: Int,
    pageSize: Int,
    keyword: String? = null,
    searchType: String? = null
) {
    var totalRecordCount = 0 // 전체 데이터 수
    var totalPageCount = 0 // 전체 페이지 수
    var startPage = 0 // 첫 페이지 번호
    var endPage = 0 // 끝 페이지 번호
    var limitStart = 0 // LIMIT 시작 위치
    var existPrevPage = false // 이전 페이지 존재 여부
    var existNextPage = false // 다음 페이지 존재 여부
    var currentPage = 1 // 현재페이지

    init {
        if (totalRecordCount > 0) {
            this.totalRecordCount = totalRecordCount
            calculation(page, recordSize, pageSize, keyword, searchType)
        }
    }

    private fun calculation(
        page: Int,
        recordSize: Int,
        pageSize: Int,
        keyword: String? = null,
        searchType: String? = null
    ) {
        currentPage = page

        // 전체 페이지 수 계산
        totalPageCount = (totalRecordCount - 1) / recordSize + 1

        // 현재 페이지 번호가 전체 페이지 수보다 큰 경우, 현재 페이지 번호에 전체 페이지 수 저장
        if (currentPage > totalPageCount) {
            currentPage = totalPageCount
        }

        // 첫 페이지 번호 계산
        startPage = (currentPage - 1) / pageSize * pageSize + 1

        // 끝 페이지 번호 계산
        endPage = startPage + pageSize - 1

        // 끝 페이지가 전체 페이지 수보다 큰 경우, 끝 페이지 전체 페이지 수 저장
        if (endPage > totalPageCount) {
            endPage = totalPageCount
        }

        // LIMIT 시작 위치 계산
        limitStart = (currentPage - 1) * recordSize

        // 이전 페이지 존재 여부 확인
        existPrevPage = startPage != 1

        // 다음 페이지 존재 여부 확인
        existNextPage = endPage * recordSize < totalRecordCount
    }
}
