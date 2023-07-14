package com.example.spring.adapter.redis.board.repository

import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(Repository::class)])
class PostRedisRepositoryTest : DescribeSpec({
//    fun extensions() = listOf(SpringExtension)

    val redisTemplate: RedisTemplate<String, Any> = mockk()
    val setOperations: SetOperations<String, Any> = mockk()

    val postRedisRepository = PostRedisRepository(redisTemplate)


    describe("PostRedisRepository") {

        beforeEach {
            clearMocks(redisTemplate, setOperations)
        }

        it("should create a like post") {
            val boardId = 2
            val postId = 2
            val email = "test@example.com"

            every { redisTemplate.opsForSet() } returns setOperations
            every { setOperations.add("like:post:$boardId:$postId", email) } returns 1L

            val result = postRedisRepository.createLikePost(boardId, postId, email)

            result shouldBe 1L
        }

        it("should delete a like post") {
            val boardId = 2
            val postId = 2
            val email = "test@example.com"

            every { redisTemplate.opsForSet() } returns setOperations
            every { setOperations.remove("like:post:$boardId:$postId", email) } returns 1L

            postRedisRepository.deleteLikePost(boardId, postId, email)

            eventually() {
                io.mockk.verify { setOperations.remove("like:post:$boardId:$postId", email)}
            }
        }

        it("should find a like post by email when value exists") {
            val boardId = 2
            val postId = 2
            val email = "test@example.com"

            every { redisTemplate.opsForSet() } returns setOperations
            every { setOperations.isMember("like:post:$boardId:$postId", email) } returns true

            val result = postRedisRepository.findLikePostByEmail(boardId, postId, email)

            result shouldBe true
        }

        it("should find a like post by email when value does not exist") {
            val boardId = 2
            val postId = 2
            val email = "test@example.com"

            every { redisTemplate.opsForSet() } returns setOperations
            every { setOperations.isMember("like:post:$boardId:$postId", email) } returns false

            val result = postRedisRepository.findLikePostByEmail(boardId, postId, email)

            result shouldBe false
        }

        it("should count the number of like posts") {
            val boardId = 2
            val postId = 2

            every { redisTemplate.opsForSet() } returns setOperations
            every { setOperations.size("like:post:$boardId:$postId") } returns 10L

            val result = postRedisRepository.countLikePost(boardId, postId)

            result shouldBe 10L
        }
    }
})
