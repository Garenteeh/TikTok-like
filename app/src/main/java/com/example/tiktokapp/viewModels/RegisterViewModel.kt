package com.example.tiktokapp.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    application: Application,
    private val registerUseCase: RegisterUseCase
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val LOGGED_USERNAME_KEY = "logged_username"

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    fun registerUser(user: User) {
        if (_registrationState.value is RegistrationState.Loading) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val result = registerUseCase("${user.firstName} ${user.lastName}", user.username, user.email, user.password)
                if (result.isSuccess) {
                    val savedUser = result.getOrNull()!!
                    // On sauvegarde le pseudo dans les prefs pour session persistante
                    prefs.edit().putString(LOGGED_USERNAME_KEY, savedUser.username).apply()
                    _registrationState.value = RegistrationState.Success
                } else {
                    val ex = result.exceptionOrNull()
                    // Map specific exceptions to field errors
                    when (ex) {
                        is com.example.tiktokapp.data.exceptions.EmailAlreadyTakenException -> {
                            _registrationState.value = RegistrationState.FieldErrors(mapOf("email" to "Email déjà utilisé"))
                        }
                        is com.example.tiktokapp.data.exceptions.UsernameAlreadyTakenException -> {
                            _registrationState.value = RegistrationState.FieldErrors(mapOf("username" to "Pseudo déjà utilisé"))
                        }
                        else -> {
                            val message = ex?.message ?: "Erreur lors de l'inscription"
                            _registrationState.value = RegistrationState.Error(message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error creating user", e)
                _registrationState.value = RegistrationState.Error("Erreur lors de l'inscription : ${e.message}")
            }
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}
