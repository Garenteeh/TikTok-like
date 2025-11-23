package com.example.tiktokapp.models

data class PrivateMessage(
    val id: int,
    val timestamp: int,
    var subject: int,
    val user: string,
    val content: string
)

