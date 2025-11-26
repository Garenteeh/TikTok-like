package com.example.tiktokapp.domain.models.dto

import com.example.tiktokapp.domain.models.Comment

data class VideoDto(
    val id: String,
    val url: String,
    val title: String,
    val user: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val reposts: Int = 0,
    val comments: List<Comment>? = emptyList()
)

