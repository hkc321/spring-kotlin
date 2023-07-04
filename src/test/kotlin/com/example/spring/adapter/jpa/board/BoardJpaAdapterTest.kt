package com.example.spring.adapter.jpa.board

import com.example.spring.adapter.jpa.board.entity.BoardJpaEntity
import com.example.spring.adapter.jpa.board.entity.PostJpaEntity
import com.example.spring.adapter.jpa.board.repository.BoardJpaRepository
import com.example.spring.adapter.jpa.board.repository.PostJpaRepository
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.adapter.jpa.member.repository.MemberJpaRepository
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.BoardJpaPort
import com.example.spring.application.service.board.PostService
import com.example.spring.domain.board.Board
import com.example.spring.domain.board.BoardTest
import com.example.spring.domain.board.Post
import com.example.spring.domain.member.Member
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.util.*


@SpringBootTest
class BoardJpaAdapterTest() {
    @Autowired
    private lateinit var boardJpaRepository: BoardJpaRepository
    @Autowired
    private lateinit var postJpaRepository: PostJpaRepository
    @Autowired
    private lateinit var postService: PostService
    @Autowired
    private lateinit var memberJpaRepository: MemberJpaRepository
    @Autowired
    private lateinit var boardJpaPort: BoardJpaPort
    @Autowired
    private lateinit var boardKotlinJdslAdapter: BoardKotlinJdslAdapter
    @Autowired
    private lateinit var postKotlinJdslAdapter: PostKotlinJdslAdapter
    @Autowired
    private lateinit var commentKotlinJdslAdapter: CommentKotlinJdslAdapter



//    @AfterEach
//    fun cleanup() {
//        boardJpaRepository.deleteAll()
//    }

    @Test
    @Transactional
    fun qwertt () {
        commentKotlinJdslAdapter.readTopLevelComment(1,1,20,1, "recnet")
    }


//    @Test
//    @Transactional
//    fun qwer() {
////        val pageable: PageRequest = PageRequest.of(0, 20)
////        var test = postKotlinJdslAdapter.readPostPageList123(1,"test","writer",pageable)
//        val test = boardJpaRepository.findByBoardId(1)
//        val bb: Board = test!!.let {
//            Board(
//                boardId = it.boardId,
//                name = it.name,
//                description = it.description,
//                writer = Member(
//                    it.writer.memberId,
//                    it.writer.email,
//                    it.writer.password,
//                    it.writer.role
//                ),
//                modifier = Member(
//                    it.modifier.memberId,
//                    it.modifier.email,
//                    it.modifier.password,
//                    it.modifier.role
//                )
//            ).apply {
//                it.posts.map {
//                    this.posts.add(
//                        Post(
//                            it.postId,
//                            Board(
//                                it.board.boardId,
//                                it.board.name,
//                                it.board.description,
//                                Member(
//                                    it.board.writer.memberId,
//                                    it.board.writer.email,
//                                    it.board.writer.password,
//                                    it.board.writer.role
//                                ),
//                                Member(
//                                    it.board.modifier.memberId,
//                                    it.board.modifier.email,
//                                    it.board.modifier.password,
//                                    it.board.modifier.role
//                                )
//                            ),
//                            it.title,
//                            it.content,
//                            Member(
//                                it.writer.memberId,
//                                it.writer.email,
//                                it.writer.password,
//                                it.writer.role
//                            )
//                        )
//                    )
//                }
//            }
//        }
//        println("1111111111111111111111")
////        bb.posts.map {
////            println(it)
////        }
//        bb.posts.add(
//            Post(
//                board = bb,
//                content = "wwwwwwwwwwwwwwwwwwwwwww",
//                title = "qwerqwer",
//                writer = bb.writer
//            )
//        )
//        boardJpaRepository.save(
//            bb.let {
//                BoardJpaEntity(
//                    boardId = it.boardId,
//                    name = it.name,
//                    description = it.description,
//                    writer = MemberJpaEntity(
//                        it.writer.memberId,
//                        it.writer.email,
//                        it.writer.password,
//                        it.writer.role
//                    ),
//                    modifier = MemberJpaEntity(
//                        it.modifier.memberId,
//                        it.modifier.email,
//                        it.modifier.password,
//                        it.modifier.role,
//                    )
//                ).apply {
//                    it.posts.map {
//                        this.posts.add(
//                            PostJpaEntity(
//                                it.postId,
//                                BoardJpaEntity(
//                                    it.board.boardId,
//                                    it.board.name,
//                                    it.board.description,
//                                    MemberJpaEntity(
//                                        it.board.writer.memberId,
//                                        it.board.writer.email,
//                                        it.board.writer.password,
//                                        it.board.writer.role,
//                                    ),
//                                    MemberJpaEntity(
//                                        it.board.modifier.memberId,
//                                        it.board.modifier.email,
//                                        it.board.modifier.password,
//                                        it.board.modifier.role,
//                                    )
//                                ),
//                                it.title,
//                                it.content,
//                                MemberJpaEntity(
//                                    it.writer.memberId,
//                                    it.writer.email,
//                                    it.writer.password,
//                                    it.writer.role
//                                )
//                            )
//                        )
//                    }
//                }
//            }
//        )
////        val qq = postJpaRepository.findById(1)
//        boardJpaRepository.save(BoardJpaEntity(
//            name = "why",
//            description = "why",
//            writer = memberJpaRepository.findByEmail("test")!!,
//            modifier = memberJpaRepository.findByEmail("test")!!
//        ))
//    }
}