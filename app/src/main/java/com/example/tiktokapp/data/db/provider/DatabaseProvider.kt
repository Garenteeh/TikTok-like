package com.example.tiktokapp.data.db.provider

import android.content.Context
import androidx.room.Room
import com.example.tiktokapp.data.db.AppDatabase

object DatabaseProvider {

    fun provide(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "tiktok_videos_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}

