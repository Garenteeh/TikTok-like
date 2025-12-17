package com.example.tiktokapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val title: String,
    val user: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val reposts: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isLiked: Boolean = false,
    val isReposted: Boolean = false
)

