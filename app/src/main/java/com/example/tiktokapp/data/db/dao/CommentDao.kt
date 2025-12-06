package com.example.tiktokapp.data.db.dao

import androidx.room.*
import com.example.tiktokapp.data.db.entities.CommentEntity

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE videoId = :videoId AND parentCommentId IS NULL ORDER BY timestamp DESC")
    suspend fun getCommentsForVideo(videoId: String): List<CommentEntity>

    @Query("SELECT * FROM comments WHERE parentCommentId = :parentCommentId ORDER BY timestamp ASC")
    suspend fun getRepliesForComment(parentCommentId: String): List<CommentEntity>

    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): CommentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Update
    suspend fun updateComment(comment: CommentEntity)

    @Query("UPDATE comments SET likes = :likes, isLiked = :isLiked WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: String, likes: Int, isLiked: Boolean)

    @Query("DELETE FROM comments WHERE videoId = :videoId")
    suspend fun deleteCommentsForVideo(videoId: String)

    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)

    @Query("DELETE FROM comments WHERE parentCommentId = :commentId")
    suspend fun deleteRepliesForComment(commentId: String)
}

