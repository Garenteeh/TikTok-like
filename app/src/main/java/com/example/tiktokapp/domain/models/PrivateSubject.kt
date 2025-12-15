package com.example.tiktokapp.domain.models

data class Conversation(
    val id: String,
    val participantUsername: String,
    val participantFirstName: String = "",
    val participantLastName: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
) {
    fun getDisplayName(): String {
        return if (participantFirstName.isNotEmpty() || participantLastName.isNotEmpty()) {
            "$participantFirstName $participantLastName".trim()
        } else {
            participantUsername
        }
    }
}

