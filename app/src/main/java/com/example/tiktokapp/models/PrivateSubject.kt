package com.example.tiktokapp.models

data class PrivateSubject(
    val id: int,
    var timestamp: int,
    var sender: int,
    val reciever: int,
    val title: String
)

