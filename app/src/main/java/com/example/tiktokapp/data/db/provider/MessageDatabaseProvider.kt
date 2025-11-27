package com.example.tiktokapp.data.db.provider

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tiktokapp.data.db.MessageDatabase
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object MessageDatabaseProvider {

    @Volatile
    private var INSTANCE: MessageDatabase? = null
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                applicationScope.launch {
                    populateDatabase(database)
                }
            }
        }
    }

    private suspend fun populateDatabase(database: MessageDatabase) {
        val subjectDao = database.subjectDao()
        val messageDao = database.messageDao()

        // Créer un sujet par défaut
        val defaultSubject = SubjectEntity(
            id = "default-subject-1",
            user = "currentUser",
            title = "Conversation avec Marie"
        )
        subjectDao.insertSubject(defaultSubject)

        // Créer des messages de test
        val messages = listOf(
            MessageEntity(
                id = "msg-1",
                subjectId = "default-subject-1",
                user = "Marie",
                content = "Salut ! Comment ça va ?",
                timestamp = System.currentTimeMillis() - 3600000
            ),
            MessageEntity(
                id = "msg-2",
                subjectId = "default-subject-1",
                user = "currentUser",
                content = "Salut Marie ! Ça va bien et toi ?",
                timestamp = System.currentTimeMillis() - 3500000
            ),
            MessageEntity(
                id = "msg-3",
                subjectId = "default-subject-1",
                user = "Marie",
                content = "Super ! Tu as vu le nouveau projet ?",
                timestamp = System.currentTimeMillis() - 3000000
            ),
            MessageEntity(
                id = "msg-4",
                subjectId = "default-subject-1",
                user = "currentUser",
                content = "Oui ! C'est vraiment intéressant",
                timestamp = System.currentTimeMillis() - 2500000
            )
        )
        messages.forEach { messageDao.insert(it) }
    }

    fun provide(context: Context): MessageDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                MessageDatabase::class.java,
                "tiktok_messages_db"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addCallback(callback)
                .build()
            INSTANCE = instance
            instance
        }
    }
}
