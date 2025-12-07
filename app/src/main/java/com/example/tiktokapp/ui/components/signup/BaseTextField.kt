package com.example.tiktokapp.ui.components.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "",
    leadingIcon: (@Composable () -> Unit) = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF2F2F2),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

//            leadingIcon()
//
//            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = value,
                singleLine = true,
                onValueChange = onValueChange,
                label = { Text(label) },
                isError = errorMessage.isNotEmpty(),
                modifier = Modifier.weight(1f),
                leadingIcon=leadingIcon,
                trailingIcon= trailingIcon,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )

            )
        }

        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseTextFieldPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BaseTextField(
                    value = "William",
                    onValueChange = {},
                    leadingIcon = { Icon(imageVector = Icons.Default.Face, contentDescription = null) },
                    label = "Prénom",
                    isError = false
                )
                BaseTextField(
                    value = "",
                    onValueChange = {},
                    label = "Nom",
                    isError = true,
                    errorMessage = "Le nom est obligatoire"
                )
            }
        }
    }
}