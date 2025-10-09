package com.example.tiktokapp.models

data class Comment(
    val id: String,
    val user: String,
    val text: String,
    val replies: List<Comment> = emptyList()
)

