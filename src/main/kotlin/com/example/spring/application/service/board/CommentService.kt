package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.application.port.out.board.CommentKotlinJdslPort
import com.example.spring.application.port.out.board.CommentRedisPort
import com.example.spring.application.service.board.exception.CommentDataNotFoundException
import com.example.spring.domain.board.Comment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentJpaPort: CommentJpaPort,
    private val commentKotlinJdslPort: CommentKotlinJdslPort,
    private val postUseCase: PostUseCase,
    private val commentRedisPort: CommentRedisPort
) : CommentUseCase {

    @Transactional
    override fun createComment(commend: CommentUseCase.Commend.CreateCommend): Comment =
        commentJpaPort.createComment(
            Comment(
                boardId = commend.boardId,
                postId = commend.postId,
                content = commend.content,
                level = commend.level,
                parentComment = commend.parentCommentId,
                writer = commend.writer
            )
        )

    @Transactional(readOnly = true)
    override fun readTopLevelComment(commend: CommentUseCase.Commend.ReadTopLevelCommend): Pair<List<Comment>, Int?> {
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))

        return commentKotlinJdslPort.readTopLevelComment(
            commend.boardId,
            commend.postId,
            commend.size,
            commend.cursor,
            commend.orderBy
        ).apply {
            this.first.map {
                it.updateIsLiked(
                    !commentRedisPort.checkCommentLikeByEmail(
                        it.boardId,
                        it.postId,
                        it.commentId,
                        commend.reader
                    )
                )
            }
        }
    }

    @Transactional(readOnly = true)
    override fun readComment(commend: CommentUseCase.Commend.ReadCommend): Comment {
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))

        return commentJpaPort.readComment(commend.boardId, commend.postId, commend.commentId)
            ?.apply {
                this.updateIsLiked(
                    !commentRedisPort.checkCommentLikeByEmail(
                        this.boardId,
                        this.postId,
                        this.commentId,
                        commend.reader
                    )
                )
            }
            ?: throw CommentDataNotFoundException(boardId = commend.boardId, postId = commend.postId, commentId = commend.commentId)
    }

    @Transactional(readOnly = true)
    override fun readChildComment(commend: CommentUseCase.Commend.ReadChildCommend): Pair<List<Comment>, Int?> {
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))
        commentJpaPort.readComment(commend.boardId, commend.postId, commend.parentCommentId)

        return commentKotlinJdslPort.readChildComment(
            commend.boardId,
            commend.postId,
            commend.parentCommentId,
            commend.size,
            commend.cursor
        ).apply {
            this.first.map {
                it.updateIsLiked(
                    !commentRedisPort.checkCommentLikeByEmail(
                        it.boardId,
                        it.postId,
                        it.commentId,
                        commend.reader
                    )
                )
            }
        }
    }

    @Transactional
    override fun updateComment(commend: CommentUseCase.Commend.UpdateCommend): Comment {
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))

        val comment: Comment = commentJpaPort.readComment(commend.boardId, commend.postId, commend.commentId)
            ?: throw CommentDataNotFoundException(boardId = commend.boardId, postId = commend.postId, commentId = commend.commentId)

        comment.update(commend.content, commend.modifier)

        return commentJpaPort.updateComment(comment).apply {
            this.updateIsLiked(
                !commentRedisPort.checkCommentLikeByEmail(
                    this.boardId,
                    this.postId,
                    this.commentId,
                    commend.modifier
                )
            )
        }
    }

    @Transactional
    override fun likeComment(commend: CommentUseCase.Commend.LikeCommend): Comment {
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))

        val comment: Comment = commentJpaPort.readComment(commend.boardId, commend.postId, commend.commentId)
            ?: throw CommentDataNotFoundException(boardId = commend.boardId, postId = commend.postId, commentId = commend.commentId)

        val likeCount =
            commentRedisPort.createCommentLike(commend.boardId, commend.postId, commend.commentId, commend.email)

        comment.updateLike(likeCount)

        return commentJpaPort.updateComment(comment).apply { this.updateIsLiked(false) }
    }

    @Transactional
    override fun deleteLikeComment(commend: CommentUseCase.Commend.LikeCommend): Comment {
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))

        val comment: Comment = commentJpaPort.readComment(commend.boardId, commend.postId, commend.commentId)
            ?: throw CommentDataNotFoundException(boardId = commend.boardId, postId = commend.postId, commentId = commend.commentId)

        val likeCount =
            commentRedisPort.deleteCommentLike(commend.boardId, commend.postId, commend.commentId, commend.email)

        comment.updateLike(likeCount)

        return commentJpaPort.updateComment(comment).apply { this.updateIsLiked(true) }
    }

    @Transactional
    override fun deleteComment(commend: CommentUseCase.Commend.DeleteCommend) {
        val comment =
            readComment(CommentUseCase.Commend.ReadCommend(commend.boardId, commend.postId, commend.commentId, commend.modifier))

        comment.checkWriter(commend.modifier)

        commentJpaPort.deleteComment(comment.boardId, comment.postId, comment.commentId)
        commentRedisPort.deleteCommentLikeAll(comment.boardId, comment.postId, comment.commentId)
    }
}