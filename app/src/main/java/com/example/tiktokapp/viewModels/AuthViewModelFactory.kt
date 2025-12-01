package com.example.tiktokapp.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tiktokapp.domain.usecases.LoginUseCase
import com.example.tiktokapp.domain.usecases.RegisterUseCase

class AuthViewModelFactory(
    private val application: Application,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(application, loginUseCase) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(application, registerUseCase) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

