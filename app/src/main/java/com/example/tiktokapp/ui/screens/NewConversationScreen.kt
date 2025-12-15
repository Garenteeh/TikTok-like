package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.viewModels.MessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConversationScreen(
    currentUsername: String,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: MessagingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle conversation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Entrez le nom d'utilisateur de la personne avec qui vous souhaitez discuter :",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    error = null
                },
                label = { Text("Nom d'utilisateur") },
                placeholder = { Text("@username") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCreating,
                isError = error != null,
                supportingText = if (error != null) {
                    { Text(error!!, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            Button(
                onClick = {
                    if (username.isBlank()) {
                        error = "Veuillez entrer un nom d'utilisateur"
                        return@Button
                    }

                    if (username.trim() == currentUsername) {
                        error = "Vous ne pouvez pas démarrer une conversation avec vous-même"
                        return@Button
                    }

                    isCreating = true
                    viewModel.createOrOpenConversation(
                        participantUsername = username.trim(),
                        currentUsername = currentUsername
                    ) { conversationId ->
                        isCreating = false
                        onNavigateToChat(conversationId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isCreating && username.isNotBlank()
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Démarrer la conversation")
                }
            }

            if (isCreating) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Note : L'utilisateur doit exister dans l'application pour pouvoir démarrer une conversation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

