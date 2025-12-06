package com.example.tiktokapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val videoId: String,
    val parentCommentId: String? = null,
    val message: String,
    val user: String,
    val timestamp: Long,
    val likes: Int = 0,
    val isLiked: Boolean = false
)

