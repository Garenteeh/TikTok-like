package com.example.tiktokapp.data.repository

import com.example.tiktokapp.data.db.dao.ConversationDao
import com.example.tiktokapp.data.db.dao.MessageDao
import com.example.tiktokapp.data.db.dao.UserDao
import com.example.tiktokapp.data.mappers.toDomain
import com.example.tiktokapp.data.mappers.toEntity
import com.example.tiktokapp.domain.models.Conversation
import com.example.tiktokapp.domain.models.Message
import com.example.tiktokapp.domain.repository.MessagingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class MessagingRepositoryImpl(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val userDao: UserDao
) : MessagingRepository {

    override fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getConversationById(conversationId: String): Conversation? {
        return conversationDao.getConversationById(conversationId)?.toDomain()
    }

    override suspend fun getOrCreateConversation(
        participantUsername: String,
        currentUsername: String
    ): Conversation {
        // Chercher une conversation existante
        val existing = conversationDao.getConversationByParticipant(participantUsername)
        if (existing != null) {
            return existing.toDomain()
        }

        // Créer une nouvelle conversation
        val participantUser = userDao.getUserByUsername(participantUsername)
        val conversationId = UUID.randomUUID().toString()

        val newConversation = Conversation(
            id = conversationId,
            participantUsername = participantUsername,
            participantFirstName = participantUser?.firstName ?: "",
            participantLastName = participantUser?.lastName ?: "",
            lastMessage = "",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0
        )

        conversationDao.insertConversation(newConversation.toEntity())
        return newConversation
    }

    override suspend fun deleteConversation(conversationId: String) {
        messageDao.deleteMessagesByConversation(conversationId)
        conversationDao.deleteConversationById(conversationId)
    }

    override fun getMessagesByConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesByConversation(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        senderUsername: String,
        content: String
    ): Message {
        val message = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderUsername = senderUsername,
            content = content,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )

        messageDao.insertMessage(message.toEntity())

        // Mettre à jour la conversation
        conversationDao.updateLastMessage(
            conversationId = conversationId,
            lastMessage = content,
            timestamp = message.timestamp
        )

        return message
    }

    override suspend fun markMessagesAsRead(conversationId: String, currentUsername: String) {
        messageDao.markAllAsRead(conversationId, currentUsername)
        conversationDao.updateUnreadCount(conversationId, 0)
    }
}

