package com.example.tiktokapp.viewModels.states

import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity

data class MessageUiState(
    val messages: List<MessageEntity> = emptyList(),
    val subject: SubjectEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
