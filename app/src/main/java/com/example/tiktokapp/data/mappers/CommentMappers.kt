package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.CommentEntity
import com.example.tiktokapp.domain.models.Comment


fun Comment.toEntity(videoId: String, parentCommentId: String? = null): CommentEntity {
    return CommentEntity(
        id = this.id,
        videoId = videoId,
        parentCommentId = parentCommentId,
        message = this.message,
        user = this.user,
        timestamp = this.timestamp,
        likes = this.likes,
        isLiked = this.isLiked
    )
}


fun CommentEntity.toDomain(replies: List<Comment> = emptyList()): Comment {
    return Comment(
        id = this.id,
        message = this.message,
        user = this.user,
        timestamp = this.timestamp,
        likes = this.likes,
        isLiked = this.isLiked,
        replies = replies
    )
}


fun flattenComments(
    comments: List<Comment>,
    videoId: String,
    parentCommentId: String? = null
): List<CommentEntity> {
    val result = mutableListOf<CommentEntity>()

    comments.forEach { comment ->
        result.add(comment.toEntity(videoId, parentCommentId))

        if (!comment.replies.isNullOrEmpty()) {
            result.addAll(flattenComments(comment.replies, videoId, comment.id))
        }
    }

    return result
}

suspend fun buildCommentTree(
    commentEntities: List<CommentEntity>,
    commentDao: com.example.tiktokapp.data.db.dao.CommentDao
): List<Comment> {
    suspend fun getReplies(commentId: String): List<Comment> {
        val replyEntities = commentDao.getRepliesForComment(commentId)
        return replyEntities.map { entity ->
            entity.toDomain(replies = getReplies(entity.id))
        }
    }

    return commentEntities.map { entity ->
        entity.toDomain(replies = getReplies(entity.id))
    }
}

