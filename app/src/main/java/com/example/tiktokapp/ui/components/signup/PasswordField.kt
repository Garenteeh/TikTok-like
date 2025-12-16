package com.example.tiktokapp.ui.components.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
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
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                val iconResource = if (passwordVisible) R.drawable.close_eye else R.drawable.eye_open
                val painter = painterResource(id = iconResource)
                Icon(
                    painter = painter,
                    contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe",
                    tint = LocalContentColor.current, // force la couleur de premier plan
                    modifier = Modifier.size(24.dp) // assure une taille visible
                )
            }
        },
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
