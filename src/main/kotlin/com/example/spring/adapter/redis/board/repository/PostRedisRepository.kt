package com.example.spring.adapter.redis.board.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class PostRedisRepository(private val redisTemplate: RedisTemplate<String, Any>) {
    fun createLikePost(boardId: Int, postId: Int, email: String): Long? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:post:$boardId:$postId"

        return valueOperations.add(key, email)
    }

    fun deleteLikePost(boardId: Int, postId: Int, email: String): Long? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:post:$boardId:$postId"

        return valueOperations.remove(key, email)
    }

    fun findLikePostByEmail(boardId: Int, postId: Int, email: String): Boolean? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:post:$boardId:$postId"

        return valueOperations.isMember(key, email)
    }

    fun countLikePost(boardId: Int, postId: Int): Long? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:post:$boardId:$postId"

        return valueOperations.size(key)
    }
}