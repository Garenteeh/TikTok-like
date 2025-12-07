package com.example.tiktokapp.ui.components.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.domain.models.Country
import com.example.tiktokapp.R
import com.example.tiktokapp.domain.models.flagEmoji

@Composable
fun CountryPicker(
    selectedCountry: Country?,
    onCountrySelected: (Country) -> Unit,
    countries: List<Country>,
    label: String = "Pays",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCountry?.name ?: "",
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                if (selectedCountry != null) {
                    Text(
                        text = selectedCountry.flagEmoji(),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_globe_24),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text(label) },
            isError = isError,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(
                    color = Color(0xFFF2F2F2),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            countries.forEach { country ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${country.flagEmoji()} ${country.name} (${country.phoneCode})")
                        }
                    },
                    onClick = {
                        expanded = false
                        onCountrySelected(country)
                    }
                )
            }
        }

        if (isError && errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCountryPicker() {
    val sampleCountries = listOf(
        Country("France", "FR", "+33"),
        Country("États-Unis", "US", "+1"),
        Country("Japon", "JP", "+81"),
        Country("Allemagne", "DE", "+49"),
        Country("Canada", "CA", "+1")
    )

    var selectedCountry by remember { mutableStateOf<Country?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        CountryPicker(
            selectedCountry = selectedCountry,
            onCountrySelected = { selectedCountry = it },
            countries = sampleCountries,
            isError = selectedCountry == null,
            errorMessage = if (selectedCountry == null) "Le pays est requis" else ""
        )
    }
}
