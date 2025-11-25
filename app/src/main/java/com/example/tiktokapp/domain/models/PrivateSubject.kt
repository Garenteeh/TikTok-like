package com.example.tiktokapp.domain.models

data class PrivateSubject(
    val id: Int,
    var timestamp: Int,
    var sender: Int,
    val receiver: Int,
    val title: String
)

