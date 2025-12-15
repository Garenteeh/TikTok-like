package com.example.tiktokapp.data.db.entities
import androidx.room.PrimaryKey
import androidx.room.Entity



@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val unreadCount: Int,
    val lastMessageTimestamp: Long,
    val lastMessage: String,
    val participantLastName: String,
    val participantFirstName: String,
    val participantUsername: String,
)





