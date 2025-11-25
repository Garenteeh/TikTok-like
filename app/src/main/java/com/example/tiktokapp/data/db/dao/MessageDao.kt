package com.example.tiktokapp.data.db.dao

import androidx.room.*
import com.example.tiktokapp.data.db.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE subjectId = :subjectId ORDER BY timestamp DESC")
    fun getForSubject(subjectId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Query("DELETE FROM message WHERE id = :messageId")
    suspend fun deleteById(messageId: String)

    @Query("DELETE FROM message WHERE subjectId = :messageId")
    suspend fun deleteBySubject(messageId: String)
}