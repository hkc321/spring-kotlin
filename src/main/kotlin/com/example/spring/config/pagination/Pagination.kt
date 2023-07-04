package com.example.spring.config.pagination

class Pagination(
    totalRecordCount: Int,
    paginationRequest: PaginationRequest
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
            calculation(paginationRequest.page, paginationRequest.recordSize, paginationRequest.pageSize)
        }
    }

    private fun calculation(
        page: Int,
        recordSize: Int,
        pageSize: Int
    ) {
        // 전체 페이지 수 계산
        totalPageCount = (totalRecordCount - 1) / recordSize + 1

        // 요청한 페이지 보정
        currentPage = when {
            0 < page && page > totalPageCount -> 1
            else -> 1
        }

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
