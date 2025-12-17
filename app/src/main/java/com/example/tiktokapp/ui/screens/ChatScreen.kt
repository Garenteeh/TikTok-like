package com.example.tiktokapp.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.domain.models.Message
import com.example.tiktokapp.ui.components.VideoPlayerChat
import com.example.tiktokapp.viewModels.MessagingViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    currentUsername: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessagingViewModel = viewModel()
) {
    val currentConversation by viewModel.currentConversation.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val currentlyPlayingId by viewModel.currentlyPlayingMessageId.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        viewModel.selectConversation(conversationId)
        viewModel.markAsRead(conversationId, currentUsername)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setCurrentlyPlayingMessageId(null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentConversation?.getDisplayName() ?: "Chargement...",
                            fontWeight = FontWeight.Bold
                        )
                        if (currentConversation != null) {
                            Text(
                                text = "@${currentConversation!!.participantUsername}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
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
        ) {
            // Liste des messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        isCurrentUser = message.senderUsername == currentUsername,
                        currentlyPlayingId = currentlyPlayingId,
                        onStartPlay = { id -> viewModel.setCurrentlyPlayingMessageId(id) },
                        onStopPlay = { viewModel.setCurrentlyPlayingMessageId(null) }
                    )
                }
            }

            // Zone de saisie
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Écrivez un message...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                try {
                                    viewModel.sendMessage(
                                        conversationId,
                                        currentUsername,
                                        messageText
                                    )
                                    messageText = ""
                                } catch (_: Exception) {
                                    Log.e("ChatScreen", "Failed to send message")
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Envoyer",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Utility to extract a usable video URL from message content
private fun extractVideoUrl(raw: String): String {
    var s = raw
    listOf("[Video]", "[video]", "video:", "VIDEO:").forEach { prefix ->
        if (s.startsWith(prefix)) s = s.removePrefix(prefix).trim()
    }

    return try {
        val decoded = URLDecoder.decode(s, StandardCharsets.UTF_8.name())
        decoded
    } catch (_: Exception) {
        s
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    currentlyPlayingId: String?,
    onStartPlay: (String) -> Unit,
    onStopPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lower = message.content.lowercase()
    val isVideo = Regex("(?i).+\\.(mp4|webm|mkv|mov)$").matches(lower)
            || lower.startsWith("content://")
            || lower.startsWith("file://")
            || lower.contains("http")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            if (isVideo) {
                val rawUrl = remember(message.content) { extractVideoUrl(message.content) }
                val uri = remember(rawUrl) {
                    try {
                        rawUrl.toUri()
                    } catch (_: Exception) {
                        Uri.EMPTY
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF2196F3),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        // Utiliser le VideoPlayerChat composable (ExoPlayer)
                        if (uri != Uri.EMPTY) {
                            VideoPlayerChat(
                                videoUrl = uri.toString(),
                                isPlaying = currentlyPlayingId == message.id,
                                modifier = Modifier.fillMaxSize(),
                                onPlayRequest = { onStartPlay(message.id) },
                                onStopRequest = { onStopPlay() }
                            )
                        } else {
                            // afficher un texte si url invalide
                            Text("Vidéo invalide", modifier = Modifier.padding(12.dp))
                        }
                    }
                }

            } else {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                    ),
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(12.dp),
                        color = if (isCurrentUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = formatMessageTime(message.timestamp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
