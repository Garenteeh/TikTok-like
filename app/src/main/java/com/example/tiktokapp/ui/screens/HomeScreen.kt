package com.example.tiktokapp.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.ui.components.CommentsBottomSheet
import com.example.tiktokapp.ui.components.VideoActionButton
import com.example.tiktokapp.ui.components.VideoCard
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = viewModel()
) {
    val videos by viewModel.videos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var selectedVideoId by remember { mutableStateOf<String?>(null) }

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

    LazyColumn(
        state = state,
        flingBehavior = fling,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = modifier
    ) {
        itemsIndexed(videos) { index, video ->

            VideoCard(
                video = video,
                isPlaying = index == centered && !scrolling,
                modifier = Modifier.height(screenHeight)
            ) {
                VideoActionButton(
                    icon = Icons.Default.Favorite,
                    text = video.formatCount(video.likes),
                    isActive = video.isLiked,
                    activeColor = androidx.compose.ui.graphics.Color.Red,
                    onClick = { viewModel.toggleLike(video.id) }
                )
                VideoActionButton(
                    icon = Icons.Default.Email,
                    text = video.formatCount(video.totalCommentsCount()),
                    onClick = { selectedVideoId = video.id }
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
                CircularProgressIndicator(Modifier.padding(16.dp))
            }
        }
    }

    selectedVideoId?.let { videoId ->
        val selectedVideo = videos.find { it.id == videoId }
        selectedVideo?.let { video ->
            CommentsBottomSheet(
                comments = video.comments ?: emptyList(),
                onDismiss = { selectedVideoId = null },
                onLikeClick = { commentId ->
                    viewModel.toggleCommentLike(video.id, commentId)
                },
                onAddComment = { message ->
                    viewModel.addComment(video.id, message)
                },
                onReplyToComment = { commentId, message ->
                    viewModel.addReplyToComment(video.id, commentId, message)
                },
                onDeleteComment = { commentId ->
                    viewModel.deleteComment(video.id, commentId)
                },
                currentUsername = "Moi",
                videoOwner = video.user
            )
        }
    }
}
