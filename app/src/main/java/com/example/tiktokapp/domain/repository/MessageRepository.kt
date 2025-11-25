package com.example.tiktokapp.domain.repository

import com.example.tiktokapp.data.db.dao.MessageDao
import com.example.tiktokapp.data.db.dao.SubjectDao
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val subjectDao: SubjectDao,
    private val messageDao: MessageDao
) {
    suspend fun getSubjectById(id: String): SubjectEntity? = subjectDao.getSubjectById(id)

    fun getSubjects(userId: String): Flow<List<SubjectEntity>> =
        subjectDao.getSubjectsByUser(userId)

    suspend fun insertSubject(subject: SubjectEntity) = subjectDao.insertSubject(subject)

    suspend fun updateSubject(subject: SubjectEntity) = subjectDao.updateSubject(subject)

    suspend fun deleteSubjectById(id: String) = subjectDao.deleteSubjectById(id)

    suspend fun deleteAllSubjects() = subjectDao.deleteAllSubjects()

    fun getMessagesBySubject(subjectId: String): Flow<List<MessageEntity>> =
        messageDao.getForSubject(subjectId)

    suspend fun insertMessage(message: MessageEntity) = messageDao.insert(message)

    suspend fun deleteMessageById(id: String) = messageDao.deleteById(id)

    suspend fun deleteMessagesBySubject(subjectId: String) =
        messageDao.deleteBySubject(subjectId)
}