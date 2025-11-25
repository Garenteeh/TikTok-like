package com.example.tiktokapp.domain.models.dto


data class CommentDto(
    val replies: List<CommentDto>? = emptyList(),
    val timestamp: Long,
    val message: String,
    val user: String,
    val id: String,
)

