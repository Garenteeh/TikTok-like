package com.example.tiktokapp.ui.components.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Téléphone",
    isError: Boolean = false,
    errorMessage: String = "Merci d'entrer un numéro de téléphone valide"
) {
    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        trailingIcon = {
            IconButton (onClick = {

            }) {
                Icon(Icons.Filled.Phone, contentDescription = "Entrer un numéro de téléphone")
            }
        },
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}

@Preview(showBackground = true)
@Composable
fun PhoneTextFieldPreview() {
    MaterialTheme {
        Surface (modifier = Modifier.padding(16.dp)) {
            Column (verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PhoneTextField(
                    value = "+33612345678",
                    onValueChange = {},
                    label = "Téléphone",
                    isError = false
                )
                PhoneTextField(
                    value = "0612",
                    onValueChange = {},
                    label = "Téléphone",
                    isError = true,
                    errorMessage = "Numéro invalide"
                )
            }
        }
    }
}