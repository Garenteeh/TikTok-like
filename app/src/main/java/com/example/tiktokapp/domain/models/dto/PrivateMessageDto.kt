package com.example.tiktokapp.domain.models.dto

data class PrivateMessageDto(
    val id: Int,
    val timestamp: Int,
    val subject: Int,
    val user: String,
    val content: String
)

