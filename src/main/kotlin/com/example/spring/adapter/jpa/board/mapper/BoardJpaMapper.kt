package com.example.spring.adapter.jpa.board.mapper

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.domain.board.Board
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface BoardJpaMapper {
    fun toBoard(dto: BoardJpaEntity?): Board

    fun toEntity(board: Board): BoardJpaEntity

    companion object {
        val INSTANCE: BoardJpaMapper = Mappers.getMapper(BoardJpaMapper::class.java)
    }
}