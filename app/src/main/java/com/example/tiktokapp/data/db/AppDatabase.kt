package com.example.tiktokapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tiktokapp.data.db.dao.UserDao
import com.example.tiktokapp.data.db.dao.VideoDao
import com.example.tiktokapp.data.db.entities.UserEntity
import com.example.tiktokapp.data.db.entities.VideoEntity

@Database(
    entities = [
        VideoEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun userDao(): UserDao
}
