package com.example.spring.adapter.rest.board.mapper

import com.example.spring.adapter.rest.board.dto.BoardCommonResponse
import com.example.spring.adapter.rest.board.dto.BoardReadPageListResponse
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.BoardTest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

@Mapper
interface BoardRestMapper {
    companion object {
        val INSTANCE: BoardRestMapper = Mappers.getMapper(BoardRestMapper::class.java)
    }

    @Mappings(
        Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
    )
    fun toBoardCommonResponse(board: Board): BoardCommonResponse

    @Mappings(
        Mapping(source = "pageable.pageNumber", target = "currentPage")
    )
    fun toBoardReadPageListResponse(pageList: Page<BoardCommonResponse>): BoardReadPageListResponse
}