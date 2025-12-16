package com.example.tiktokapp.domain.usecases

import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    // Accept a full User so callers can provide phone, birthDate and country.
    suspend operator fun invoke(user: User): Result<User> {
        if ((user.firstName + user.lastName).isBlank() || user.email.isBlank() || user.password.isBlank()) {
            return Result.failure(IllegalArgumentException("Name, email and password cannot be empty"))
        }
        return authRepository.register(user)
    }
}
