package com.example.tiktokapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.R
import com.example.tiktokapp.data.utils.UserUtils.validateAll
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.models.Country
import com.example.tiktokapp.repositories.CountryRepository
import com.example.tiktokapp.ui.components.signup.BaseTextField
import com.example.tiktokapp.ui.components.signup.BirthDateTextField
import com.example.tiktokapp.ui.components.signup.CountryPicker
import com.example.tiktokapp.ui.components.signup.PasswordTextField
import com.example.tiktokapp.ui.components.signup.PhoneTextField
import com.example.tiktokapp.viewModels.RegistrationState
import com.example.tiktokapp.viewModels.UserViewModel

@Composable
fun SignupScreen(
    userViewModel: UserViewModel,
    onNavigateToLogin: () -> Unit,
    onSignupSucess: () -> Unit
) {
    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var username: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var phoneNumber: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    var confirmPassword: String by remember { mutableStateOf("") }
    var birthDate: String by remember { mutableStateOf("") }
    var selectedCountry: Country? by remember { mutableStateOf<Country?>(null) }

    val errors = remember { mutableStateMapOf<String, String>() }

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val registrationState by userViewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationState.FieldErrors -> {
                val fieldErrors = (registrationState as RegistrationState.FieldErrors).errors
                errors.clear()
                errors.putAll(fieldErrors)
            }
            is RegistrationState.Success -> {
                Toast.makeText(context, "Inscription réussie", Toast.LENGTH_SHORT).show()
                errors.clear()
                userViewModel.resetState()
                onSignupSucess()
            }
            is RegistrationState.Error -> {
                val message = (registrationState as RegistrationState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                userViewModel.resetState()
            }
            else -> {}
        }
    }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Inscription", style = MaterialTheme.typography.headlineMedium)

        // First Name
        BaseTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                errors.remove("firstName")
            },
            label = "Prénom",
            errorMessage = errors["firstName"] ?: "",
            isError =  errors.containsKey("firstName"),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null
                )
            }
        )

        // Last Name
        BaseTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                errors.remove("lastName")
            },
            label = "Nom",
            errorMessage = errors["lastName"] ?: "",
            isError =  errors.containsKey("lastName"),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        )

        // Username
        BaseTextField(
            value = username,
            onValueChange = {
                username = it
                errors.remove("username")
            },
            label = "Nom d'utilisateur",
            errorMessage = errors["username"] ?: "",
            isError =  errors.containsKey("username"),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_alternate_email_24),
                    contentDescription = null
                )
            }
        )

        // Email
        BaseTextField(
            value = email,
            onValueChange = {
                email = it
                errors.remove("email")
            },
            label = "Email",
            errorMessage = errors["email"] ?: "",
            isError =  errors.containsKey("email"),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },

        )

        // Phone Number
        PhoneTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                errors.remove("phone")
           },
            label = "Numéro de téléphone",
            errorMessage = errors["phone"] ?: "",
            isError =  errors.containsKey("phone"),
        )

        // Password
        PasswordTextField (
            value = password,
            onValueChange = {
                password = it
                errors.remove("password")
            },
            label = "Mot de passe",
            errorMessage = errors["password"] ?: "",
            isError =  errors.containsKey("password")

        )

        PasswordTextField (
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errors.remove("confirmPassword")
            },
            label = "Confirmation du Mot de passe",
            errorMessage = errors["confirmPassword"] ?: "",
            isError =  errors.containsKey("confirmPassword")

        )

        // Birth Date
        BirthDateTextField (
            value = birthDate,
            onValueChange = {
                birthDate = it
                errors.remove("birthDate")
            },
            error = errors["birthDate"] ?: "",
            isError =  errors.containsKey("birthDate")
        )


        // Country Picker
        CountryPicker(
            selectedCountry = selectedCountry,
            onCountrySelected = {
                selectedCountry = it
                errors.remove("country")
            },
            countries = CountryRepository.countries,
            label = "Pays",
            isError = errors.containsKey("country"),
            errorMessage = errors["country"] ?: ""
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val countryName = selectedCountry?.name ?: ""
                val validationErrors = validateAll(
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    email = email,
                    password = password,
                    phone = phoneNumber,
                    country = countryName,
                    birthDate = birthDate,
                    confirmPassword = confirmPassword
                )

                if (validationErrors.isEmpty()) {
                    errors.clear()
                    val newUser = User(
                        firstName = firstName,
                        lastName = lastName,
                        username = username,
                        email = email,
                        password = password,
                        phoneNumber = phoneNumber,
                        country = countryName,
                        birthDate = birthDate
                    )
                    userViewModel.registerUser(newUser)

                } else {
                    errors.putAll(validationErrors)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("S'inscrire")
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = onNavigateToLogin) {
                Text("Déjà un compte ? Se connecter")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}