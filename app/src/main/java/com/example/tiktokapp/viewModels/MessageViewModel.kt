package com.example.tiktokapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity
import com.example.tiktokapp.domain.repository.MessageRepository
import com.example.tiktokapp.viewModels.states.MessageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MessageViewModel(
    private val repository: MessageRepository,
    private val userId: String,
    private val subjectId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSubject()
        loadMessages()
    }

    private fun loadSubject() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val subject = repository.getSubjectById(subjectId)
                _uiState.value = _uiState.value.copy(
                    subject = subject,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            try {
                repository.getMessagesBySubject(subjectId).collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erreur de chargement messages: ${e.message}"
                )
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            try {
                val message = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    subjectId = subjectId,
                    user = userId,
                    content = content
                )
                repository.insertMessage(message)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            try {
                repository.deleteMessageById(messageId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
