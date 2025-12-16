package com.example.tiktokapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.ui.components.signup.BaseTextField
import com.example.tiktokapp.ui.components.signup.PasswordTextField
import com.example.tiktokapp.viewModels.UserViewModel
import com.example.tiktokapp.viewModels.RegistrationState

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onNavigateToSignup: () -> Unit,
    onLoginSucess: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val errors = remember { mutableStateMapOf<String, String>() }

    val context = LocalContext.current
    val registrationState by userViewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationState.FieldErrors -> {
                errors.clear()
                errors.putAll((registrationState as RegistrationState.FieldErrors).errors)
            }
            is RegistrationState.Success -> {
                Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()
                errors.clear()
                userViewModel.resetState()
                onLoginSucess()
            }
            is RegistrationState.Error -> {
                val message = (registrationState as RegistrationState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                userViewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BaseTextField(
                value = identifier,
                onValueChange = {
                    identifier = it
                    errors.remove("identifier")
                },
                label = "Pseudo ou Email",
                errorMessage = errors["identifier"] ?: "",
                isError = errors.containsKey("identifier"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            PasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    errors.remove("password")
                },
                label = "Mot de passe",
                errorMessage = errors["password"] ?: "",
                isError = errors.containsKey("password"),
            )

            Button(
                onClick = { userViewModel.loginUser(identifier, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se connecter")
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onNavigateToSignup) {
                    Text("Pas encore de compte ? S'inscrire")
                }
            }
        }
    }
}
