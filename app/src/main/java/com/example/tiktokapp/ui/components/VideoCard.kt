package com.example.tiktokapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.models.Video

@Composable
fun VideoCard(
    video: Video,
    modifier: Modifier = Modifier,
    actionButtons: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // TODO: Remplacer par le composant vidéo réel (ExoPlayer)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            color = Color.Black.copy(alpha = 0.4f),
            shape = androidx.compose.material3.MaterialTheme.shapes.small
        ) {
            Text(
                text = video.user,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color.White
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            color = Color.Black.copy(alpha = 0.4f),
            shape = androidx.compose.material3.MaterialTheme.shapes.small
        ) {
            Text(
                text = video.title,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(12.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                actionButtons()
            }
        }
    }
}
