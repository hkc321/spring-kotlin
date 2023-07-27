package com.example.spring.adapter.redis.board.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class CommentRedisRepository(private val redisTemplate: RedisTemplate<String, Any>) {
    fun createLikeComment(boardId: Int, postId: Int, commentId: Int, email: String): Long? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:comment:$boardId:$postId:$commentId"

        return valueOperations.add(key, email)
    }

    fun deleteLikeComment(boardId: Int, postId: Int, commentId: Int, email: String): Long? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:comment:$boardId:$postId:$commentId"

        return valueOperations.remove(key, email)
    }

    fun findLikeCommentByEmail(boardId: Int, postId: Int, commentId: Int, email: String): Boolean? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:comment:$boardId:$postId:$commentId"

        return valueOperations.isMember(key, email)
    }

    fun countLikeComment(boardId: Int, postId: Int, commentId: Int): Long? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:comment:$boardId:$postId:$commentId"

        return valueOperations.size(key)
    }

    fun deleteLikeCommentAll(boardId: Int, postId: Int, commentId: Int): Any? {
        val valueOperations = redisTemplate.opsForSet()
        val key = "like:comment:$boardId:$postId:$commentId"

        return valueOperations.pop(key)
    }
}