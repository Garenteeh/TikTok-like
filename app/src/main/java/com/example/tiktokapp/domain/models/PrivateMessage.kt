package com.example.tiktokapp.domain.models

data class Message(
    val id: String,
    val conversationId: String,
    val senderUsername: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

