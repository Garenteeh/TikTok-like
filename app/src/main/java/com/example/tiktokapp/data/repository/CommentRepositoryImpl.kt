package com.example.tiktokapp.data.repository

import android.util.Log
import com.example.tiktokapp.data.db.dao.CommentDao
import com.example.tiktokapp.data.mappers.buildCommentTree
import com.example.tiktokapp.data.mappers.flattenComments
import com.example.tiktokapp.domain.models.Comment
import com.example.tiktokapp.domain.repository.CommentRepository

class CommentRepositoryImpl(
    private val commentDao: CommentDao
) : CommentRepository {

    /**
     * Get all comments for a specific video with their replies hierarchy
     */
    override suspend fun getCommentsForVideo(videoId: String): List<Comment> {
        return try {
            val commentEntities = commentDao.getCommentsForVideo(videoId)
            buildCommentTree(commentEntities, commentDao)
        } catch (e: Exception) {
            Log.e("CommentRepositoryImpl", "Error getting comments for video $videoId", e)
            emptyList()
        }
    }

    /**
     * Save a new comment to the database
     * If parentCommentId is provided, it will be saved as a reply
     */
    override suspend fun saveComment(videoId: String, comment: Comment, parentCommentId: String?) {
        try {
            val commentEntities = flattenComments(listOf(comment), videoId, parentCommentId)
            commentDao.insertComments(commentEntities)
            Log.d("CommentRepositoryImpl", "Saved comment ${comment.id} to DB")
        } catch (e: Exception) {
            Log.e("CommentRepositoryImpl", "Error saving comment", e)
        }
    }

    /**
     * Save multiple comments (useful for API sync)
     * Flattens the entire comment tree and saves all comments and replies
     */
    override suspend fun saveComments(videoId: String, comments: List<Comment>) {
        try {
            if (comments.isEmpty()) return

            val commentEntities = flattenComments(comments, videoId)
            commentDao.insertComments(commentEntities)
            Log.d("CommentRepositoryImpl", "Saved ${commentEntities.size} comments for video $videoId")
        } catch (e: Exception) {
            Log.e("CommentRepositoryImpl", "Error saving comments", e)
        }
    }

    /**
     * Update a comment's like status in the database
     */
    override suspend fun updateCommentLike(commentId: String, likes: Int, isLiked: Boolean) {
        try {
            commentDao.updateCommentLike(commentId, likes, isLiked)
            Log.d("CommentRepositoryImpl", "Updated like for comment $commentId: likes=$likes, isLiked=$isLiked")
        } catch (e: Exception) {
            Log.e("CommentRepositoryImpl", "Error updating comment like", e)
        }
    }

    /**
     * Delete all comments for a specific video
     */
    override suspend fun deleteCommentsForVideo(videoId: String) {
        try {
            commentDao.deleteCommentsForVideo(videoId)
            Log.d("CommentRepositoryImpl", "Deleted all comments for video $videoId")
        } catch (e: Exception) {
            Log.e("CommentRepositoryImpl", "Error deleting comments for video", e)
        }
    }

    /**
     * Delete all comments from the database
     */
    override suspend fun deleteAllComments() {
        try {
            commentDao.deleteAllComments()
            Log.d("CommentRepositoryImpl", "Deleted all comments")
        } catch (e: Exception) {
            Log.e("CommentRepositoryImpl", "Error deleting all comments", e)
        }
    }
}

