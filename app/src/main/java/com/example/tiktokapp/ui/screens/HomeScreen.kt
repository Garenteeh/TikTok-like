package com.example.tiktokapp.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.ui.components.CommentsBottomSheet
import com.example.tiktokapp.ui.components.BottomBar
import com.example.tiktokapp.ui.components.VideoActionButton
import com.example.tiktokapp.ui.components.VideoCard
import com.example.tiktokapp.viewModels.VideoListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToAddVideo: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = viewModel(),
    currentUsername: String = "Moi"
) {
    val videos by viewModel.videos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var selectedVideoId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(videos) {
        Log.d("HomeScreen", "Videos updated: ${videos.size} videos")
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { videos.size })

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= videos.size - 5 && !isLoading && videos.size < 30) {
            viewModel.loadMoreVideos()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        containerColor = Color.Transparent,
        bottomBar = {
            BottomBar(onHome = {}, onAdd = onNavigateToAddVideo, onProfile = onNavigateToProfile, onMessages = onNavigateToMessages)
        }

    ) { paddingValues ->
        val videoHeight = screenHeight - paddingValues.calculateBottomPadding()

        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            val video = videos[page]
            val isCurrentPage = pagerState.currentPage == page

            VideoCard(
                video = video,
                isPlaying = isCurrentPage,
                modifier = Modifier.height(videoHeight)
            ) {
                VideoActionButton(
                    icon = Icons.Default.Favorite,
                    text = video.formatCount(video.likes),
                    isActive = video.isLiked,
                    activeColor = Color.Red,
                    onClick = { viewModel.toggleLike(video.id) }
                )
                VideoActionButton(
                    icon = Icons.Default.Email,
                    text = video.formatCount(video.totalCommentsCount()),
                    onClick = { selectedVideoId = video.id }
                )
                VideoActionButton(
                    icon = Icons.Default.Share,
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out this video: ${video.url}")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }
                )
                VideoActionButton(
                    icon = Icons.Default.Refresh,
                    text = video.formatCount(video.reposts),
                    isActive = video.isReposted,
                    activeColor = Color.Green,
                    onClick = { viewModel.toggleRepost(video.id) }
                )
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
                    viewModel.addComment(video.id, message, currentUsername)
                },
                onReplyToComment = { commentId, message ->
                    viewModel.addReplyToComment(video.id, commentId, message, currentUsername)
                },
                onDeleteComment = { commentId ->
                    viewModel.deleteComment(video.id, commentId, currentUsername)
                },
                currentUsername = currentUsername,
                videoOwner = video.user
            )
        }
    }
}

@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
