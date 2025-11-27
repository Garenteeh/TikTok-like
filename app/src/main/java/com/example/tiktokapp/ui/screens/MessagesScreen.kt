package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.ui.components.MessageInputField
import com.example.tiktokapp.ui.components.MessageItem
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import com.example.tiktokapp.viewModels.MessageViewModel
import com.example.tiktokapp.viewModels.states.MessageUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: MessageViewModel,
    currentUserId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text(uiState.subject?.title ?: "Messages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            MessageInputField(
                onSendMessage = { message ->
                    viewModel.sendMessage(message)
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        MessagesContent(
            uiState = uiState,
            currentUserId = currentUserId,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun MessagesContent(
    uiState: MessageUiState,
    currentUserId: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        uiState.subject?.let { subject ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = subject.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "avec ${subject.user}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Erreur: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            uiState.messages.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun message dans cette conversation",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                val listState = rememberLazyListState()

                LaunchedEffect(uiState.messages.size) {
                    if (uiState.messages.isNotEmpty()) {
                        listState.animateScrollToItem(uiState.messages.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    reverseLayout = false
                ) {
                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        MessageItem(
                            message = message,
                            isCurrentUser = message.user == currentUserId
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessagesContentPreview() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = listOf(
                    MessageEntity(
                        id = "1",
                        subjectId = "s1",
                        user = "Jean",
                        content = "Salut !",
                        timestamp = System.currentTimeMillis() - 3600000
                    ),
                    MessageEntity(
                        id = "2",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Salut ! Comment ça va ?",
                        timestamp = System.currentTimeMillis() - 3000000
                    ),
                    MessageEntity(
                        id = "3",
                        subjectId = "s1",
                        user = "Jean",
                        content = "Très bien merci ! Je voulais te parler du projet",
                        timestamp = System.currentTimeMillis() - 1800000
                    ),
                    MessageEntity(
                        id = "4",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Oui bien sûr, je t'écoute",
                        timestamp = System.currentTimeMillis() - 300000
                    )
                )
            ),
            currentUserId = "currentUser"
        )
    }
}
