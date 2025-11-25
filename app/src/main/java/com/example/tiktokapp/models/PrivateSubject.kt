package com.example.tiktokapp.models

data class PrivateSubject(
    val id: Int,
    var timestamp: Int,
    var sender: Int,
    val reciever: Int,
    val title: String
)

