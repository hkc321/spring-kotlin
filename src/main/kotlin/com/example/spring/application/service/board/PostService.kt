package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.PostJpaPort
import com.example.spring.application.port.out.board.PostKotlinJdslPort
import com.example.spring.application.port.out.board.PostRedisPort
import com.example.spring.application.service.board.exception.PostDataNotFoundException
import com.example.spring.application.service.board.exception.PostLikeException
import com.example.spring.config.code.ErrorCode
import com.example.spring.domain.board.Post
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostService(
    private val postJpaPort: PostJpaPort,
    private val postKotlinJdslPort: PostKotlinJdslPort,
    private val boardUseCase: BoardUseCase,
    private val postRedisPort: PostRedisPort
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
    override fun readPost(commend: PostUseCase.Commend.ReadCommend): Post {
        val board = boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))

        return postKotlinJdslPort.readPost(board, commend.postId)
            ?.apply {
                this.updateIsLiked(
                    !postRedisPort.checkPostLikeByEmail(
                        this.boardId,
                        this.postId,
                        commend.reader
                    )
                )
            } ?: throw PostDataNotFoundException(boardId = commend.boardId, postId = commend.postId)
    }

    @Transactional
    override fun updatePost(commend: PostUseCase.Commend.UpdateCommend): Post {
        val board = boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        val post: Post = postKotlinJdslPort.readPost(board, commend.postId)
            ?: throw PostDataNotFoundException(boardId = commend.boardId, postId = commend.postId)

        post.update(commend.title, commend.content, commend.modifier)

        return postJpaPort.updatePost(post).apply {
            this.updateIsLiked(
                !postRedisPort.checkPostLikeByEmail(
                    this.boardId,
                    this.postId,
                    commend.modifier
                )
            )
        }
    }

    @Transactional
    override fun likePost(commend: PostUseCase.Commend.LikeCommend): Post {
        val likeCount = postRedisPort.createPostLike(commend.boardId, commend.postId, commend.email)
            ?: throw PostLikeException(
                boardId = commend.boardId,
                postId = commend.postId,
                code = ErrorCode.ALREADY_EXIST,
                message = "이미 좋아요를 클릭한 게시글입니다. [boardId: ${commend.boardId}, postId: ${commend.postId}]"
            )

        val board = boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        val post: Post = postKotlinJdslPort.readPost(board, commend.postId)
            ?: throw PostDataNotFoundException(boardId = commend.boardId, postId = commend.postId)

        post.updateLike(likeCount)

        return postJpaPort.updatePost(post).apply { this.updateIsLiked(false) }
    }

    @Transactional
    override fun deleteLikePost(commend: PostUseCase.Commend.LikeCommend): Post {
        val likeCount = postRedisPort.deletePostLike(commend.boardId, commend.postId, commend.email)
            ?: throw PostLikeException(
                boardId = commend.boardId,
                postId = commend.postId,
                code = ErrorCode.DATA_NOT_FOUND,
                message = "좋아요를 클릭한 이력이 없습니다. [boardId: ${commend.boardId}, postId: ${commend.postId}]"
            )

        val board = boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        val post: Post = postKotlinJdslPort.readPost(board, commend.postId)
            ?: throw PostDataNotFoundException(boardId = commend.boardId, postId = commend.postId)

        post.updateLike(likeCount)

        return postJpaPort.updatePost(post).apply { this.updateIsLiked(true) }
    }

    @Transactional
    override fun deletePost(commend: PostUseCase.Commend.DeleteCommend) {
        val board = boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        val post = postKotlinJdslPort.readPost(board, commend.postId)
            ?: throw PostDataNotFoundException(boardId = commend.boardId, postId = commend.postId)

        post.checkWriter(commend.modifier)

        postJpaPort.deletePost(board, post.postId)
        postRedisPort.deletePostLikeAll(commend.boardId, commend.postId)
    }
}