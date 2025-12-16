package com.example.tiktokapp.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.exceptions.EmailAlreadyTakenException
import com.example.tiktokapp.data.exceptions.UsernameAlreadyTakenException
import com.example.tiktokapp.data.repository.UserRepositoryImpl
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.UserRepository
import com.example.tiktokapp.domain.usecases.LoginUseCase
import com.example.tiktokapp.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Combined ViewModel for login and register to simplify injection and state handling.
 */
class UserViewModel(
    application: Application,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val LOGGED_USERNAME_KEY = "logged_username"

    private val database = DatabaseProvider.provide(application)
    private val userRepository: UserRepository = UserRepositoryImpl(userDao = database.userDao())

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

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
                        Log.d("UserViewModel", "Restored logged user: ${u.username}")
                    }
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Error restoring user", e)
                } finally {
                    _restored.value = true
                }
            }
        } else {
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
                Log.e("UserViewModel", "Login error", e)
                _registrationState.value = RegistrationState.Error("Erreur lors de la connexion : ${e.message}")
            }
        }
    }

    fun registerUser(user: User) {
        if (_registrationState.value is RegistrationState.Loading) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                // pass the full User so extra fields are persisted and _currentUser can expose them
                val result = registerUseCase(user)
                if (result.isSuccess) {
                    val savedUser = result.getOrNull()!!
                    prefs.edit().putString(LOGGED_USERNAME_KEY, savedUser.username).apply()
                    _currentUser.value = savedUser.copy(password = "", salt = "")
                    _registrationState.value = RegistrationState.Success
                } else {
                    when (val ex = result.exceptionOrNull()) {
                        is EmailAlreadyTakenException -> {
                            _registrationState.value = RegistrationState.FieldErrors(mapOf("email" to "Email déjà utilisé"))
                        }
                        is UsernameAlreadyTakenException -> {
                            _registrationState.value = RegistrationState.FieldErrors(mapOf("username" to "Pseudo déjà utilisé"))
                        }
                        else -> {
                            val message = ex?.message ?: "Erreur lors de l'inscription"
                            _registrationState.value = RegistrationState.Error(message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error creating user", e)
                _registrationState.value = RegistrationState.Error("Erreur lors de l'inscription : ${e.message}")
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


