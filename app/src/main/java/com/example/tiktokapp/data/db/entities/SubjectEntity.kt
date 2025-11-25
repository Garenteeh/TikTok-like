package com.example.tiktokapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject")
data class SubjectEntity(
    @PrimaryKey
    val id: String,
    val user: String,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String
)

