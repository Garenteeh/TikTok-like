package com.example.tiktokapp.domain.models

data class Comment(
    val id: String,
    val message: String,
    val user: String,
    val timestamp: Long,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<Comment>? = emptyList()
)


