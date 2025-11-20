package com.example.tiktokapp.models

data class Comment(
    val id: String,
    val user: String,
    val message: String,
    val timestamp: Long,
    val replies: List<Comment>? = emptyList()
)

