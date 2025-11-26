package com.example.tiktokapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tiktokapp.domain.repository.MessageRepository

class MessageViewModelFactory(
    private val repository: MessageRepository,
    private val userId: String,
    private val subjectId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(repository, userId, subjectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
