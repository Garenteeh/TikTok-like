package com.example.tiktokapp.domain.usecases

import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Name, email and password cannot be empty"))
        }
        return authRepository.register(name, email, password)
    }
}

