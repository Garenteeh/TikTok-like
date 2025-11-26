package com.example.tiktokapp.viewModels.states

import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity

data class SubjectUiState(
    val subjects: List<SubjectEntity> = emptyList(),
    val selectedSubject: SubjectEntity? = null,
    val messages: List<MessageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
