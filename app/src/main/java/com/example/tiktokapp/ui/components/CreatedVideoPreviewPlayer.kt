package com.example.tiktokapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.PlayerView

@Composable
fun CreatedVideoPreviewPlayer(
    videoUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controlsEnabled = false
    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(1024, 10_240, 250, 500)
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setRenderersFactory(
                DefaultRenderersFactory(context)
                    .setEnableDecoderFallback(true)
            )
            .setLoadControl(loadControl)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = false
            }
    }
    var frameRendered by remember { mutableStateOf(false) }
    var playRequested by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    val playerListener = remember {
        object : Player.Listener {
            override fun onRenderedFirstFrame() {
                frameRendered = true
                if (playRequested) {
                    try { exoPlayer.play() } catch (_: Exception) {}
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
            }
        }
    }
    DisposableEffect(exoPlayer) {
        exoPlayer.addListener(playerListener)
        onDispose { exoPlayer.removeListener(playerListener) }
    }
    LaunchedEffect(videoUrl) {
        frameRendered = false
        playRequested = isPlaying
        if (videoUrl.isNotBlank()) {
            try {
                exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
                exoPlayer.playWhenReady = false
                exoPlayer.prepare()
            } catch (_: Exception) {
            }
        } else {
            try { exoPlayer.stop() } catch (_: Exception) { }
        }
    }

    var tappedPlayState by remember { mutableStateOf(isPlaying) }

    LaunchedEffect(isPlaying) {
        tappedPlayState = isPlaying
        if (isPlaying) {
            playRequested = true
            if (frameRendered) {
                try { exoPlayer.play() } catch (_: Exception) {}
            }
        } else {
            playRequested = false
            try { exoPlayer.pause() } catch (_: Exception) {}
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                Lifecycle.Event.ON_DESTROY -> {
                    try { exoPlayer.release() } catch (_: Exception) {}
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try { exoPlayer.release() } catch (_: Exception) {}
        }
    }

    Box(modifier = modifier) {
        val clickModifier = if (controlsEnabled) {
            Modifier.pointerInput(Unit) {
                detectTapGestures {
                    tappedPlayState = !tappedPlayState
                    if (tappedPlayState) exoPlayer.play() else exoPlayer.pause()
                }
            }
        } else {
            Modifier
        }

        AndroidView(
            modifier = Modifier
                .then(clickModifier)
                .fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = RESIZE_MODE_FIT
                }
            }
        )

        if (isBuffering || (!frameRendered && exoPlayer.playbackState == Player.STATE_BUFFERING)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
    }
}
