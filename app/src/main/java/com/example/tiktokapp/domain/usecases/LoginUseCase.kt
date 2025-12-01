package com.example.tiktokapp.domain.usecases

import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        }
        return authRepository.login(email, password)
    }
}

