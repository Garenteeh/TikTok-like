package com.example.tiktokapp.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView

/**
 * VideoPlayerChat: composable réutilisable pour lire des vidéos dans le chat et le share.
 * - Charge ExoPlayer de façon paresseuse (création seulement quand nécessaire)
 * - Joue/arrête sur clic via callbacks
 * - Nettoie le player à la destruction
 * - Ne gère pas le singleton global: le ViewModel doit garder l'id du message actuellement joué
 *
 * Inputs:
 * - videoUrl: url de la vidéo (String)
 * - isPlaying: boolean contrôlé par le parent (true si ce message doit jouer)
 * - modifier: modifier compose
 * - height: hauteur souhaitée
 * - onPlayRequest: appelé quand l'utilisateur clique pour démarrer la lecture
 * - onStopRequest: appelé quand l'utilisateur clique pour arrêter la lecture
 */

@Composable
fun VideoPlayerChat(
    videoUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    onPlayRequest: () -> Unit = {},
    onStopRequest: () -> Unit = {}
) {
    val context = LocalContext.current

    // State pour indiquer si la ressource est prête
    var isBuffering by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    // Créer le player uniquement si une URL est fournie
    val playerDisposable = remember(videoUrl) { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(videoUrl) {
        // initialisation paresseuse; ne crée que si url non vide
        if (videoUrl.isNotBlank()) {
            val p = try {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItem = MediaItem.fromUri(videoUrl.toUri())
                    setMediaItem(mediaItem)
                    playWhenReady = false
                    repeatMode = Player.REPEAT_MODE_OFF
                }
            } catch (e: Exception) {
                loadError = e.localizedMessage ?: "Erreur au chargement"
                null
            }
            playerDisposable.value = p
        }

        onDispose {
            playerDisposable.value?.run {
                stop()
                release()
            }
            playerDisposable.value = null
        }
    }

    // Effet : synchroniser isPlaying avec le player
    LaunchedEffect(isPlaying, playerDisposable.value) {
        val p = playerDisposable.value
        if (p != null) {
            if (isPlaying) {
                try {
                    isBuffering = true
                    p.prepare()
                    p.playWhenReady = true
                } catch (e: Exception) {
                    loadError = e.localizedMessage
                } finally {
                    isBuffering = false
                }
            } else {
                p.playWhenReady = false
                p.pause()
            }
        }
    }

    Surface(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (isPlaying) onStopRequest() else onPlayRequest()
                }, contentAlignment = Alignment.Center
        ) {
            if (loadError != null) {
                Text(text = "Vidéo invalide", color = MaterialTheme.colorScheme.error)
                return@Box
            }

            val p = playerDisposable.value
            if (p != null) {
                AndroidView(factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = p
                        useController = false
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                }, modifier = modifier.fillMaxSize())

                if (isBuffering) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp))
                }
            } else {
                // aucun player disponible (url vide ou erreur)
                Text(
                    text = "Aucune vidéo",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
