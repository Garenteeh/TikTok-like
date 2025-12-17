package com.example.tiktokapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tiktokapp.data.db.dao.CommentDao
import com.example.tiktokapp.data.db.dao.ConversationDao
import com.example.tiktokapp.data.db.dao.MessageDao
import com.example.tiktokapp.data.db.dao.UserDao
import com.example.tiktokapp.data.db.dao.VideoDao
import com.example.tiktokapp.data.db.entities.CommentEntity
import com.example.tiktokapp.data.db.entities.ConversationEntity
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.UserEntity
import com.example.tiktokapp.data.db.entities.VideoEntity

@Database(
    entities = [
        VideoEntity::class,
        UserEntity::class,
        CommentEntity::class,
        ConversationEntity::class,
        MessageEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
