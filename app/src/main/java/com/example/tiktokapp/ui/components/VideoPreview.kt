package com.example.tiktokapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun VideoPreview(
    videoUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    CreatedVideoPreviewPlayer(videoUrl = videoUrl, isPlaying = isPlaying, modifier = modifier)
}
