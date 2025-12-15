package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.ConversationEntity
import com.example.tiktokapp.domain.models.Conversation

fun ConversationEntity.toDomain(): Conversation {
    return Conversation(
        id = id,
        participantUsername = participantUsername,
        participantFirstName = participantFirstName,
        participantLastName = participantLastName,
        lastMessage = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount
    )
}

fun Conversation.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = id,
        participantUsername = participantUsername,
        participantFirstName = participantFirstName,
        participantLastName = participantLastName,
        lastMessage = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount
    )
}



