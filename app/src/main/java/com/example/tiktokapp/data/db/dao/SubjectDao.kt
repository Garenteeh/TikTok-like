package com.example.tiktokapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tiktokapp.data.db.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject WHERE user = :userId ORDER BY timestamp DESC")
    fun getSubjectsByUser(userId: String): Flow<List<SubjectEntity>>

    @Query("SELECT COUNT(*) FROM subject WHERE user = :userId")
    suspend fun getSubjectsCountByUser(userId: String): Int

    @Query("SELECT * FROM subject WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Query("DELETE FROM subject WHERE id = :id")
    suspend fun deleteSubjectById(id: String)

    @Query("DELETE FROM subject")
    suspend fun deleteAllSubjects()
}