package com.example.spring.config.redis

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class CustomPageImpl<T> @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @JsonProperty("content") content: List<T>?,
    @JsonProperty("number") page: Int,
    @JsonProperty("size") size: Int,
    @JsonProperty("totalElements") total: Long
) :
    PageImpl<T>(content!!, PageRequest.of(page, size), total) {
    @JsonIgnore
    override fun getPageable(): Pageable {
        return super.getPageable()
    }
}