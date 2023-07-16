package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.PostRedisPort
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PostServiceDbTest {
    @Autowired
    private lateinit var postService: PostService

//    @Test
//    fun likePost() {
//        for(i: Int in 1..10000){
//            postService.likePost(PostUseCase.Commend.LikeCommend(2,2, "test$i"))
//        }
//        val ww = postService.readPost(PostUseCase.Commend.ReadCommend(2,2))
//        Assertions.assertEquals(10000, ww.like)
//    }

}