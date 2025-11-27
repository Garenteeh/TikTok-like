package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.CommentEntity
import com.example.tiktokapp.domain.models.Comment

/**
 * Convertit un Comment domain model en CommentEntity pour la DB
 * Attention: cette fonction ne gère que le commentaire lui-même, pas ses réponses
 */
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

/**
 * Convertit un CommentEntity en Comment domain model
 * Note: Les réponses doivent être chargées séparément et ajoutées
 */
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

/**
 * Aplatit récursivement un arbre de commentaires en liste plate pour la DB
 * Chaque commentaire garde une référence à son parent via parentCommentId
 */
fun flattenComments(
    comments: List<Comment>,
    videoId: String,
    parentCommentId: String? = null
): List<CommentEntity> {
    val result = mutableListOf<CommentEntity>()

    comments.forEach { comment ->
        // Ajouter le commentaire lui-même
        result.add(comment.toEntity(videoId, parentCommentId))

        // Ajouter récursivement toutes ses réponses
        if (!comment.replies.isNullOrEmpty()) {
            result.addAll(flattenComments(comment.replies, videoId, comment.id))
        }
    }

    return result
}

/**
 * Reconstruit l'arbre de commentaires à partir d'une liste plate
 * Crée la structure hiérarchique en regroupant les réponses sous leurs parents
 */
suspend fun buildCommentTree(
    commentEntities: List<CommentEntity>,
    commentDao: com.example.tiktokapp.data.db.dao.CommentDao
): List<Comment> {
    // Récupérer récursivement les réponses pour un commentaire
    suspend fun getReplies(commentId: String): List<Comment> {
        val replyEntities = commentDao.getRepliesForComment(commentId)
        return replyEntities.map { entity ->
            entity.toDomain(replies = getReplies(entity.id))
        }
    }

    // Construire l'arbre pour les commentaires principaux
    return commentEntities.map { entity ->
        entity.toDomain(replies = getReplies(entity.id))
    }
}

