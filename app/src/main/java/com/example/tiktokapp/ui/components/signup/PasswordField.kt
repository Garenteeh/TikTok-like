package com.example.tiktokapp.ui.components.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.R

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Mot de passe",
    isError: Boolean,
    errorMessage: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                val iconResource = if (passwordVisible) R.drawable.close_eye else R.drawable.eye_open
                val painter = painterResource(id = iconResource)
                Icon(
                    painter = painter, // Utilisez le paramètre 'painter'
                    contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"
                )
                Icon(painter = painter, contentDescription = "Toggle password visibility")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordTextFieldPreview() {
    MaterialTheme {
        Surface (modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PasswordTextField(
                    value = "password123",
                    onValueChange = {},
                    label = "Mot de passe",
                    isError = false
                )
                PasswordTextField(
                    value = "123",
                    onValueChange = {},
                    label = "Mot de passe",
                    isError = true,
                    errorMessage = "Mot de passe trop court"
                )
            }
        }
    }
}
