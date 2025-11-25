package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.R
import com.example.tiktokapp.models.Country
import com.example.tiktokapp.models.UserValidation.validateAll
import com.example.tiktokapp.repositories.CountryRepository
import com.example.tiktokapp.ui.components.signup.BaseTextField
import com.example.tiktokapp.ui.components.signup.BirthDateTextField
import com.example.tiktokapp.ui.components.signup.CountryPicker
import com.example.tiktokapp.ui.components.signup.PasswordTextField
import com.example.tiktokapp.ui.components.signup.PhoneTextField

@Composable
fun SignupScreen() {
    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var username: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var phoneNumber: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    var confirmPassword: String by remember { mutableStateOf("") }
    var birthDate: String by remember { mutableStateOf("") }
    var selectedCountry: Country? by remember { mutableStateOf<Country?>(null) }

    // États pour erreurs
    var errors by remember { mutableStateOf(mutableMapOf<String, String>()) }

    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Inscription", style = MaterialTheme.typography.headlineMedium)

        // First Name
        BaseTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                errors["firstName"] = ""
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
                errors["lastName"] = ""
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
                errors["username"] = ""
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
                errors["email"] = ""
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
                errors["phone"] = ""
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
                errors["password"] = ""
            },
            label = "Mot de passe",
            errorMessage = errors["password"] ?: "",
            isError =  errors.containsKey("password")

        )

        PasswordTextField (
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errors["confirmPassword"] = ""
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
                errors["birthDate"] = ""
            },
            error = errors["birthDate"] ?: "",
            isError =  errors.containsKey("birthDate")
        )


        // Country Picker
        CountryPicker(
            selectedCountry = selectedCountry,
            onCountrySelected = {
                selectedCountry = it
                errors["country"] = ""
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
                // Appel de la fonction de validation centrale
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
                    // Aucun problème : procéder à l'inscription
                    errors = mutableMapOf()
                    // TODO: envoyer les données au backend ou stocker localement
                } else {
                    // Il y a des erreurs : les afficher
                    errors = validationErrors
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("S'inscrire")
        }
    }
}