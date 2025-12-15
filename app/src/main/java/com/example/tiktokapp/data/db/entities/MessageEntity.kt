package com.example.tiktokapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val senderUsername: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean
)

