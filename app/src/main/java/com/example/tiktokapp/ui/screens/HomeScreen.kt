package com.example.tiktokapp.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.ui.components.BottomBar
import com.example.tiktokapp.ui.components.VideoActionButton
import com.example.tiktokapp.ui.components.VideoCard
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun HomeScreen(
    onNavigateToAddVideo: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = viewModel(),
) {
    val videos by viewModel.videos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(videos) {
        Log.d("HomeScreen", "Videos updated: ${videos.size} videos")
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val state = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(state)

    val centered by remember {
        derivedStateOf {
            val info = state.layoutInfo
            val items = info.visibleItemsInfo
            if (items.isEmpty()) 0 else {
                val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
                items.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - center) }
                    ?.index ?: 0
            }
        }
    }

    val scrolling by remember { derivedStateOf { state.isScrollInProgress } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomBar(onHome = {}, onAdd = onNavigateToAddVideo, onProfile = onNavigateToProfile)
        }
    ) { paddingValues ->
        LazyColumn(
            state = state,
            flingBehavior = fling,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(videos) { index, video ->

                VideoCard(
                    video = video,
                    isPlaying = index == centered && !scrolling,
                    modifier = Modifier.height(screenHeight)
                ) {
                    VideoActionButton(Icons.Default.Favorite, "${video.likes}") {}
                    VideoActionButton(Icons.Default.Email, "${video.totalCommentsCount()}") {}
                    VideoActionButton(Icons.Default.Share) {}
                    VideoActionButton(Icons.Default.Refresh) {}
                }

                if (index >= videos.lastIndex - 2 && !isLoading) {
                    viewModel.loadMoreVideos()
                }
            }

            if (isLoading) {
                item {
                    CircularProgressIndicator(Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
