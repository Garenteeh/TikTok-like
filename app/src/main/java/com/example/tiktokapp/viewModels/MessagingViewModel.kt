package com.example.tiktokapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.repository.MessagingRepositoryImpl
import com.example.tiktokapp.domain.models.Conversation
import com.example.tiktokapp.domain.models.Message
import com.example.tiktokapp.domain.repository.MessagingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessagingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseProvider.provide(application)
    private val repository: MessagingRepository = MessagingRepositoryImpl(
        conversationDao = database.conversationDao(),
        messageDao = database.messageDao(),
        userDao = database.userDao()
    )

    // Liste des conversations
    val conversations: StateFlow<List<Conversation>> = repository.getAllConversations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Conversation actuelle
    private val _currentConversation = MutableStateFlow<Conversation?>(null)
    val currentConversation: StateFlow<Conversation?> = _currentConversation.asStateFlow()

    // Messages de la conversation actuelle
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Erreurs
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun selectConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val conversation = repository.getConversationById(conversationId)
                _currentConversation.value = conversation

                if (conversation != null) {
                    repository.getMessagesByConversation(conversationId)
                        .collect { messageList ->
                            _messages.value = messageList
                        }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du chargement de la conversation"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createOrOpenConversation(participantUsername: String, currentUsername: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val conversation = repository.getOrCreateConversation(participantUsername, currentUsername)
                _currentConversation.value = conversation
                onCreated(conversation.id)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de la création de la conversation"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(conversationId: String, senderUsername: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            try {
                repository.sendMessage(conversationId, senderUsername, content)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de l'envoi du message"
            }
        }
    }

    fun markAsRead(conversationId: String, currentUsername: String) {
        viewModelScope.launch {
            try {
                repository.markMessagesAsRead(conversationId, currentUsername)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du marquage des messages"
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                repository.deleteConversation(conversationId)
                if (_currentConversation.value?.id == conversationId) {
                    _currentConversation.value = null
                    _messages.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de la suppression"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

