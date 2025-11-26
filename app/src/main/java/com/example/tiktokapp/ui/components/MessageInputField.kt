package com.example.tiktokapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.ui.theme.TikTokAppTheme

@Composable
fun MessageInputField(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Écrire un message...",
    maxLines: Int = 3
) {
    var messageText by remember { mutableStateOf("") }

    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 3.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text(placeholder) },
                modifier = Modifier.weight(1f),
                maxLines = maxLines,
                shape = MaterialTheme.shapes.medium
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                },
                enabled = messageText.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (messageText.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (messageText.isNotBlank()) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer")
            }
        }
    }
}

@Preview(showBackground = true, name = "MessageInputField - Vide")
@Composable
private fun MessageInputFieldPreviewEmpty() {
    TikTokAppTheme {
        MessageInputField(
            onSendMessage = {}
        )
    }
}

@Preview(showBackground = true, name = "MessageInputField - Avec texte")
@Composable
private fun MessageInputFieldPreviewWithText() {
    TikTokAppTheme {
        var text by remember { mutableStateOf("Bonjour, comment allez-vous ?") }
        MessageInputField(
            onSendMessage = { text = "" }
        )
    }
}
