package com.example.tiktokapp.domain.repository

import com.example.tiktokapp.domain.models.User

interface UserRepository {
    suspend fun createUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByUsername(username: String): User?

    suspend fun login(identifier: String, password: String): User?
}