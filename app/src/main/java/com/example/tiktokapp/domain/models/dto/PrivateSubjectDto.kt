package com.example.tiktokapp.domain.models.dto

data class PrivateSubjectDto(
    val id: Int,
    val timestamp: Int,
    val sender: Int,
    val receiver: Int,
    val title: String
)

