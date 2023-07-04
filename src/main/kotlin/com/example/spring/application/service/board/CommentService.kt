package com.example.spring.application.service.board

import com.example.spring.application.port.`in`.board.BoardUseCase
import com.example.spring.application.port.`in`.board.CommentUseCase
import com.example.spring.application.port.`in`.board.PostUseCase
import com.example.spring.application.port.out.board.CommentJpaPort
import com.example.spring.application.port.out.board.CommentKotlinJdslPort
import com.example.spring.domain.board.Comment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentJpaPort: CommentJpaPort,
    private val commentKotlinJdslPort: CommentKotlinJdslPort,
    private val boardUseCase: BoardUseCase,
    private val postUseCase: PostUseCase,
) : CommentUseCase {

    @Transactional
    override fun createComment(commend: CommentUseCase.Commend.CreateCommend): Comment =
        commentJpaPort.createComment(
            Comment(
                board = commend.boardId,
                post = commend.postId,
                content = commend.content,
                level = commend.level,
                parentComment = commend.parentCommentId,
                writer = commend.writer
            )
        )

    @Transactional(readOnly = true)
    override fun readTopLevelComment(commend: CommentUseCase.Commend.ReadTopLevelCommend): Pair<List<Comment>, Int?> {
        boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))

        return commentKotlinJdslPort.readTopLevelComment(
            commend.boardId,
            commend.postId,
            commend.size,
            commend.cursor,
            commend.orderBy
        )
    }

    @Transactional(readOnly = true)
    override fun readComment(commend: CommentUseCase.Commend.ReadCommend): Comment =
        commentJpaPort.readComment(commend.boardId, commend.postId, commend.commentId)

    @Transactional(readOnly = true)
    override fun readChildComment(commend: CommentUseCase.Commend.ReadChildCommend): Pair<List<Comment>, Int?> {
        boardUseCase.readBoard(BoardUseCase.Commend.ReadCommend(commend.boardId))
        postUseCase.readPost(PostUseCase.Commend.ReadCommend(commend.boardId, commend.postId))
        commentJpaPort.readComment(commend.boardId, commend.postId, commend.parentCommentId)

        return commentKotlinJdslPort.readChildComment(
            commend.boardId,
            commend.postId,
            commend.parentCommentId,
            commend.size,
            commend.cursor
        )
    }

    @Transactional
    override fun updateComment(commend: CommentUseCase.Commend.UpdateCommend): Comment {
        val comment: Comment =
            commentJpaPort.readComment(
                boardId = commend.boardId,
                postId = commend.postId,
                commentId = commend.commentId
            )
        comment.update(commend.content)

        return commentJpaPort.updateComment(comment)
    }

    @Transactional(readOnly = true)
    override fun deleteComment(commend: CommentUseCase.Commend.DeleteCommend) =
        commentJpaPort.deleteComment(commend.boardId, commend.postId, commend.commentId)
}