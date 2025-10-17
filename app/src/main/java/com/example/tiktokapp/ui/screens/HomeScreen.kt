package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.ui.components.VideoActionButton
import com.example.tiktokapp.ui.components.VideoCard
import com.example.tiktokapp.viewModels.VideoListViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = viewModel()
) {
    val videos by viewModel.videos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    val spaceBetween = 0.dp

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        verticalArrangement = Arrangement.spacedBy(spaceBetween),
        modifier = modifier,
    ) {
        itemsIndexed(videos) { index, video ->
            VideoCard(
                video = video,
                modifier = Modifier
                    .then(Modifier)
                    .padding(0.dp)
            ) {
                VideoActionButton(
                    icon = Icons.Default.Favorite,
                    text = "${video.likes}",
                    onClick = { /* TODO: Like */ }
                )
                VideoActionButton(
                    icon = Icons.Default.Email,
                    text = "${video.comments.size}",
                    onClick = { /* TODO: Show comments */ }
                )
                VideoActionButton(
                    icon = Icons.Default.Share,
                    onClick = { /* TODO: Share */ }
                )
                VideoActionButton(
                    icon = Icons.Default.Refresh,
                    onClick = { /* TODO: Repost */ }
                )
            }

            if (index >= videos.lastIndex - 2 && !isLoading) {
                viewModel.loadMoreVideos()
            }
        }

        if (isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
