package com.example.tiktokapp.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.repository.UserRepositoryImpl
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.UserRepository
import com.example.tiktokapp.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val loginUseCase: LoginUseCase
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val LOGGED_USERNAME_KEY = "logged_username"

    private val database = DatabaseProvider.provide(application)
    private val userRepository: UserRepository = UserRepositoryImpl(userDao = database.userDao())

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Indicateur que la tentative de restauration a été effectuée (true même si user est null)
    private val _restored = MutableStateFlow(false)
    val restored: StateFlow<Boolean> = _restored.asStateFlow()

    init {
        val savedUsername = prefs.getString(LOGGED_USERNAME_KEY, null)
        if (!savedUsername.isNullOrBlank()) {
            viewModelScope.launch {
                try {
                    val u = userRepository.getUserByUsername(savedUsername)
                    if (u != null) {
                        _currentUser.value = u
                        Log.d("LoginViewModel", "Restored logged user: ${u.username}")
                    }
                } catch (e: Exception) {
                    Log.e("LoginViewModel", "Error restoring user", e)
                } finally {
                    _restored.value = true
                }
            }
        } else {
            // Pas d'username sauvegardé -> restauration terminée
            _restored.value = true
        }
    }

    fun loginUser(identifier: String, password: String) {
        if (_registrationState.value is RegistrationState.Loading) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                if (identifier.isBlank()) {
                    _registrationState.value = RegistrationState.FieldErrors(mapOf("identifier" to "Email ou pseudo requis"))
                    return@launch
                }
                if (password.isBlank()) {
                    _registrationState.value = RegistrationState.FieldErrors(mapOf("password" to "Mot de passe requis"))
                    return@launch
                }

                val emailToUse = if (identifier.contains("@")) {
                    identifier
                } else {
                    val byUsername = userRepository.getUserByUsername(identifier)
                    byUsername?.email ?: run {
                        _registrationState.value = RegistrationState.FieldErrors(mapOf("identifier" to "Identifiant ou mot de passe incorrect"))
                        return@launch
                    }
                }

                val result = loginUseCase(emailToUse, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _currentUser.value = user.copy(password = "", salt = "")
                    prefs.edit().putString(LOGGED_USERNAME_KEY, user.username).apply()
                    _registrationState.value = RegistrationState.Success
                } else {
                    _registrationState.value = RegistrationState.FieldErrors(mapOf("identifier" to "Identifiant ou mot de passe incorrect"))
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error", e)
                _registrationState.value = RegistrationState.Error("Erreur lors de la connexion : ${e.message}")
            }
        }
    }

    fun logout() {
        prefs.edit().remove(LOGGED_USERNAME_KEY).apply()
        _currentUser.value = null
        _registrationState.value = RegistrationState.Idle
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}
