package com.example.tiktokapp.domain.repository

import com.example.tiktokapp.domain.models.AuthToken
import com.example.tiktokapp.domain.models.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, username: String, email: String, password: String): Result<User>
    suspend fun refreshToken(): Result<AuthToken>
    suspend fun logout(): Result<Unit>
    suspend fun isLoggedIn(): Boolean
}
