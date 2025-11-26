package com.example.tiktokapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.domain.models.Video

@Composable
fun VideoCard(
    video: Video,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    actionButtons: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {

        VideoPlayer(
            videoUrl = video.url,
            isPlaying = isPlaying,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = video.user,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Text(
                text = video.title,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                actionButtons()
            }
        }
    }
}
