package com.example.tiktokapp.viewModels

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
    data class FieldErrors(val errors: Map<String, String>) : RegistrationState()
}

