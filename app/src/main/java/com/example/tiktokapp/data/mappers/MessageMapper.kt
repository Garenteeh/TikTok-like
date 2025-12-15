package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.domain.models.Message

fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        conversationId = conversationId,
        senderUsername = senderUsername,
        content = content,
        timestamp = timestamp,
        isRead = isRead
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        conversationId = conversationId,
        senderUsername = senderUsername,
        content = content,
        timestamp = timestamp,
        isRead = isRead
    )
}

