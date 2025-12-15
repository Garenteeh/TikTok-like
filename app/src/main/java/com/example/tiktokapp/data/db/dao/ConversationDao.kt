package com.example.tiktokapp.data.db.dao

import androidx.room.*
import com.example.tiktokapp.data.db.entities.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE participantUsername = :participantUsername LIMIT 1")
    suspend fun getConversationByParticipant(participantUsername: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversationById(conversationId: String)

    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastMessageTimestamp = :timestamp WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId: String, lastMessage: String, timestamp: Long)

    @Query("UPDATE conversations SET unreadCount = :count WHERE id = :conversationId")
    suspend fun updateUnreadCount(conversationId: String, count: Int)
}
