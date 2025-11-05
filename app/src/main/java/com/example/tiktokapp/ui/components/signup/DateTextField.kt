package com.example.tiktokapp.ui.components.signup

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Date de naissance",
    isError: Boolean = false,
    errorMessage: String = "Merci de sélectionner une date"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val dateString = remember (value) { value?.let { dateFormat.format(it) } ?: "" }

    if (value.isNotBlank()) {
        try {
            val date = dateFormat.parse(value)
            if (date != null) calendar.time = date
        } catch (_: Exception) { }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val formatted = dateFormat.format(calendar.time)
            onValueChange(formatted)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    BaseTextField(
        value = dateString,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        errorMessage = errorMessage,
        trailingIcon = {
            IconButton (onClick = {
                datePickerDialog.show()
            }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Choisir une date")
            }
        },
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default
    )
}

@Preview(showBackground = true)
@Composable
fun DateTextFieldPreview() {
    MaterialTheme {
        Surface (modifier = Modifier.padding(16.dp)) {
            Column (verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DateTextField(
                    value = "01/01/2000",
                    onValueChange = {},
                    label = "Date de naissance",
                    isError = false
                )
                DateTextField(
                    value = "",
                    onValueChange = {},
                    label = "Date de naissance",
                    isError = true,
                    errorMessage = "Veuillez sélectionner une date"
                )
            }
        }
    }
}
