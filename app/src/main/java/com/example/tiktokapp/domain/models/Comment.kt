package com.example.tiktokapp.domain.models

data class Comment(
    val replies: List<Comment>? = emptyList(),
    val timestamp: Long,
    val message: String,
    val user: String,
    val id: String,
    )


