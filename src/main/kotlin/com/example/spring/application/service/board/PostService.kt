package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.domain.board.Post
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostService(
    private val postJpaPort: PostJpaPort,
    private val postKotlinJdslPort: PostKotlinJdslPort,
    private val boardUseCase: BoardUseCase
) : PostUseCase {

    @Transactional
    override fun createPost(commend: PostUseCase.Commend.CreateCommend): Post =
        postJpaPort.createPost(
            Post(
                boardId = commend.boardId,
                title = commend.title,
                content = commend.content,
                writer = commend.writer
            )
        )

    @Transactional(readOnly = true)
    override fun readPostPageList(commend: PostUseCase.Commend.ReadListCommend): Page<Post> =
        postKotlinJdslPort.readPostPageList(commend.boardId, commend.keyword, commend.searchType, commend.pageable)

    @Transactional(readOnly = true)
    override fun readPost(commend: PostUseCase.Commend.ReadCommend): Post =
        postKotlinJdslPort.readPost(
            boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId)),
            commend.postId
        )

    @Transactional
    override fun updatePost(commend: PostUseCase.Commend.UpdateCommend): Post {
        val post: Post = postKotlinJdslPort.readPost(
            boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId)),
            commend.postId
        )
        post.update(commend.title, commend.content, commend.modifier)

        return postJpaPort.updatePost(post)
    }

    @Transactional
    override fun deletePost(commend: PostUseCase.Commend.DeleteCommend) {
        val board = boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        val post = postKotlinJdslPort.readPost(board, commend.postId)
        post.checkWriter(commend.modifier)

        postJpaPort.deletePost(
            board,
            post.postId
        )
    }
}