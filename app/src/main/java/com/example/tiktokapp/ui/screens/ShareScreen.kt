package com.example.tiktokapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.viewModels.MessagingViewModel
import com.example.tiktokapp.ui.components.VideoPlayerChat
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import com.example.tiktokapp.domain.models.Conversation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    currentUsername: String,
    sharedVideo: String?,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessagingViewModel = viewModel()
) {
    var shareError by remember { mutableStateOf<String?>(null) }

    val conversations by viewModel.conversations.collectAsState()

    val decodedSharedVideo: String? = remember(sharedVideo) {
        sharedVideo?.let {
            try {
                URLDecoder.decode(it, StandardCharsets.UTF_8.name())
            } catch (_: Exception) {
                it
            }
        }
    }

    var pendingConversationId by remember { mutableStateOf<String?>(null) }
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                pendingConversationId?.let { convId ->
                    viewModel.sendMessage(convId, currentUsername, it.toString())
                    onNavigateToChat(convId)
                }
            }
        }
    )

    // quick input field for sharing to a new recipient (separate from the creation area)
    var quickUsername by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partager une vidéo", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1) Aperçu de la vidéo à envoyer
            if (!decodedSharedVideo.isNullOrBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    ), modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Vidéo à envoyer", fontWeight = FontWeight.Bold)
                        val videoUrl = try {
                            decodedSharedVideo.toUri().toString()
                        } catch (_: Exception) {
                            null
                        }
                        var isPlaying by rememberSaveable { mutableStateOf(true) }
                        if (!videoUrl.isNullOrBlank()) {
                            VideoPlayerChat(
                                videoUrl = videoUrl,
                                isPlaying = isPlaying,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                onPlayRequest = { isPlaying = true },
                                onStopRequest = { isPlaying = false }
                            )
                        } else {
                            Text("Vidéo invalide", modifier = Modifier.padding(12.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick share input: partage rapide vers un pseudo existant ou nouveau
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = quickUsername,
                                onValueChange = { quickUsername = it; shareError = null },
                                placeholder = { Text("@pseudo") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
                                val target = quickUsername.trim()
                                if (target.isBlank()) {
                                    shareError = "Entrez un nom d'utilisateur"
                                    return@Button
                                }
                                if (target == currentUsername) {
                                    shareError =
                                        "Vous ne pouvez pas démarrer une conversation avec vous-même"
                                    return@Button
                                }
                                viewModel.createOrOpenConversation(
                                    target,
                                    currentUsername
                                ) { convId ->
                                    viewModel.sendMessage(
                                        convId,
                                        currentUsername,
                                        decodedSharedVideo
                                    )
                                    onNavigateToChat(convId)
                                }
                                quickUsername = ""
                            }) {
                                Text("Partager")
                            }
                        }

                        if (!shareError.isNullOrBlank()) Text(
                            shareError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Section de partage externe (bouton unique qui ouvre le chooser ACTION_SEND)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                ), modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Partager en externe", fontWeight = FontWeight.Bold)

                    val context = LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                // Construire le texte de partage avec l'URL décodée si présente
                                val shareText = if (!decodedSharedVideo.isNullOrBlank()) {
                                    "Check out this video: $decodedSharedVideo"
                                } else {
                                    "Check out this content"
                                }

                                // Intent standard ACTION_SEND -> chooser
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            }, modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Partage externe ")
                            Icon(Icons.Filled.Share, contentDescription = "Partage externe")
                        }
                    }
                }
            }

            Text("Conversations existantes", style = MaterialTheme.typography.titleSmall)

            if (conversations.isEmpty()) {
                Text(
                    "Aucune conversation",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    conversations.forEach { conv ->
                        CompactConversationRow(
                            conversation = conv,
                            onShareVideo = {
                                if (!decodedSharedVideo.isNullOrBlank()) {
                                    viewModel.sendMessage(
                                        conv.id,
                                        currentUsername,
                                        decodedSharedVideo
                                    )
                                    onNavigateToChat(conv.id)
                                } else {
                                    pendingConversationId = conv.id
                                    videoPickerLauncher.launch("video/*")
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CompactConversationRow(
    conversation: Conversation,
    onShareVideo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShareVideo)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Afficher uniquement le pseudo en petite taille
        Text(
            text = conversation.getDisplayName(),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onShareVideo) {
                Icon(
                    imageVector = Icons.Filled.Share, contentDescription = "Partager"
                )
            }
        }
    }
}