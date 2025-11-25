package com.example.tiktokapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tiktokapp.data.db.entities.SubjectEntity
import com.example.tiktokapp.data.db.entities.VideoEntity

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject WHERE user = :id")
    suspend fun getSubjectsByUser(id: String): List<SubjectEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Query(" val subject: String,DELETE FROM subject")
    suspend fun deleteAllSubjects()

    @Query("DELETE FROM subject WHERE id = :id;")
    suspend fun deleteSubjectById(id: String)
}

