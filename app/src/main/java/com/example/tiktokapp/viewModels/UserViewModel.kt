package com.example.tiktokapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.exceptions.EmailAlreadyTakenException
import com.example.tiktokapp.data.exceptions.UsernameAlreadyTakenException
import com.example.tiktokapp.data.repository.UserRepositoryImpl
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Initialisation de la DB et du Repository comme dans VideoListViewModel
    private val database = DatabaseProvider.provide(application)
    private val userRepository: UserRepository = UserRepositoryImpl(
        userDao = database.userDao()
    )

    // État pour suivre le résultat de l'inscription
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerUser(user: User) {
        if (_registrationState.value is RegistrationState.Loading) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                userRepository.createUser(user)
                Log.d("UserViewModel", "User created successfully: ${user.username}")
                _registrationState.value = RegistrationState.Success
            } catch (e: EmailAlreadyTakenException) {
                Log.e("UserViewModel", "Email already taken", e)
                _registrationState.value = RegistrationState.FieldErrors(mapOf("email" to "Email déjà utilisé"))
            } catch (e: UsernameAlreadyTakenException) {
                Log.e("UserViewModel", "Username already taken", e)
                _registrationState.value = RegistrationState.FieldErrors(mapOf("username" to "Nom d'utilisateur déjà utilisé"))
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error creating user", e)
                _registrationState.value = RegistrationState.Error("Erreur lors de l'inscription : ${e.message}")
            }
        }
    }

    // Pour réinitialiser l'état après avoir affiché un Toast ou navigué
    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}

// Classe scellée pour définir les différents états de l'inscription
sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
    data class FieldErrors(val errors: Map<String, String>) : RegistrationState()
}
