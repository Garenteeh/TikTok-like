package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = viewModel()
) {
    val videos by viewModel.videos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val visibleIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {

                val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                val centerItem = visibleItems.minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                }
                centerItem?.index ?: 0
            } else 0
        }
    }


    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = modifier
    ) {
        itemsIndexed(videos) { index, video ->
            VideoCard(
                video = video,
                isPlaying = index == visibleIndex,
                modifier = Modifier.height(screenHeight)
            ) {
                VideoActionButton(
                    icon = Icons.Default.Favorite,
                    text = "${video.likes}",
                    onClick = {}
                )
                VideoActionButton(
                    icon = Icons.Default.Email,
                    text = "${video.totalCommentsCount()}",
                    onClick = {}
                )
                VideoActionButton(
                    icon = Icons.Default.Share,
                    onClick = {}
                )
                VideoActionButton(
                    icon = Icons.Default.Refresh,
                    onClick = {}
                )
            }

            if (index >= videos.lastIndex - 2 && !isLoading) {
                viewModel.loadMoreVideos()
            }
        }

        if (isLoading) {
            item {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}