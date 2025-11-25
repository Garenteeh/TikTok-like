package com.example.tiktokapp.models

data class PrivateMessage(
    val id: Int,
    val timestamp: Int,
    var subject: Int,
    val user: String,
    val content: String
)

