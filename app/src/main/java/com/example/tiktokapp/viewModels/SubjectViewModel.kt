package com.example.tiktokapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity
import com.example.tiktokapp.domain.repository.MessageRepository
import com.example.tiktokapp.viewModels.states.SubjectUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SubjectViewModel(
    private val repository: MessageRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            repository.getSubjects(userId).collect { subjects ->
                _uiState.value = _uiState.value.copy(subjects = subjects)
            }
        }
    }

    fun selectSubject(subject: SubjectEntity) {
        _uiState.value = _uiState.value.copy(selectedSubject = subject)
        loadMessages(subject.id)
    }

    private fun loadMessages(subjectId: String) {
        viewModelScope.launch {
            repository.getMessagesBySubject(subjectId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun createSubject(title: String) {
        viewModelScope.launch {
            val subject = SubjectEntity(
                id = UUID.randomUUID().toString(),
                user = userId,
                title = title
            )
            repository.insertSubject(subject)
        }
    }

    fun sendMessage(content: String) {
        val subjectId = _uiState.value.selectedSubject?.id ?: return
        viewModelScope.launch {
            val message = MessageEntity(
                id = UUID.randomUUID().toString(),
                subjectId = subjectId,
                user = userId,
                content = content
            )
            repository.insertMessage(message)
        }
    }

    fun deleteSubject(subjectId: String) {
        viewModelScope.launch {
            repository.deleteMessagesBySubject(subjectId)
            repository.deleteSubjectById(subjectId)
            if (_uiState.value.selectedSubject?.id == subjectId) {
                _uiState.value = _uiState.value.copy(
                    selectedSubject = null,
                    messages = emptyList()
                )
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessageById(messageId)
        }
    }
}