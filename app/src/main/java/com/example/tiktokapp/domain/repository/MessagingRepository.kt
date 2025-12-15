package com.example.tiktokapp.domain.repository


import kotlinx.coroutines.flow.Flow
import com.example.tiktokapp.domain.models.Message
import com.example.tiktokapp.domain.models.Conversation

interface MessagingRepository {

    suspend fun markMessagesAsRead(conversationId: String, currentUsername: String)
    suspend fun sendMessage(
        conversationId: String,
        senderUsername: String,
        content: String
    ): Message

    fun getMessagesByConversation(conversationId: String): Flow<List<Message>>
    // Messages

    suspend fun deleteConversation(conversationId: String)
    suspend fun getOrCreateConversation(
        participantUsername: String,
        currentUsername: String
    ): Conversation

    suspend fun getConversationById(conversationId: String): Conversation?
    fun getAllConversations(): Flow<List<Conversation>>
}


