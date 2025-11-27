package com.example.tiktokapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tiktokapp.data.db.dao.MessageDao
import com.example.tiktokapp.data.db.dao.SubjectDao
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity

@Database(
    entities = [SubjectEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun messageDao(): MessageDao
}
