package com.example.tiktokapp.domain.repository

import com.example.tiktokapp.domain.models.Comment

interface CommentRepository {

    /**
     * Get all comments for a specific video
     */
    suspend fun getCommentsForVideo(videoId: String): List<Comment>

    /**
     * Save a new comment to the database
     */
    suspend fun saveComment(videoId: String, comment: Comment, parentCommentId: String? = null)

    /**
     * Save multiple comments (useful for API sync)
     */
    suspend fun saveComments(videoId: String, comments: List<Comment>)

    /**
     * Update a comment's like status
     */
    suspend fun updateCommentLike(commentId: String, likes: Int, isLiked: Boolean)

    /**
     * Delete all comments for a specific video
     */
    suspend fun deleteCommentsForVideo(videoId: String)

    /**
     * Delete all comments
     */
    suspend fun deleteAllComments()

    /**
     * Delete a specific comment and all its replies
     */
    suspend fun deleteComment(commentId: String)
}

