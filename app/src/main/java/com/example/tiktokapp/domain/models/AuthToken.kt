package com.example.tiktokapp.domain.models

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long // seconds
)

