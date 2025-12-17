package com.example.tiktokapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.ui.components.BottomBar
import com.example.tiktokapp.ui.components.CreateVideoControls
import com.example.tiktokapp.ui.components.CreatedVideoPreviewPlayer
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun CreateVideoScreen(
    viewModel: VideoListViewModel = viewModel(),
    currentUsername: String = "Moi",
    onSaved: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {}
) {
    val context = LocalContext.current

    val isSaving by viewModel.isSaving.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val saved by viewModel.saved.observeAsState(false)

    var title by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> pendingUri = uri }
    )

    val recordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val dataUri: Uri? = result.data?.data
            pendingUri = dataUri
        }
    }

    LaunchedEffect(pendingUri) {
        val newUri = pendingUri
        if (newUri != null) {
            videoUri = null
            kotlinx.coroutines.delay(120)
            videoUri = newUri
        }
    }

    LaunchedEffect(saved) {
        if (saved) {
            Toast.makeText(context, "Vidéo sauvegardée", Toast.LENGTH_SHORT).show()
            viewModel.clearSavedFlag()
            onSaved()
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                onHome = onNavigateHome,
                onProfile = onNavigateToProfile,
                onAdd = {/* current */},
                onMessages = onNavigateToMessages
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (videoUri != null) {
                CreatedVideoPreviewPlayer(
                    videoUrl = videoUri.toString(),
                    isPlaying = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Aucune vidéo sélectionnée", color = Color.White)
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    CreateVideoControls(
                        title = title,
                        onTitleChange = { title = it },
                        onPickClick = { pendingUri = null; pickVideoLauncher.launch("video/*") },
                        onRecordClick = { pendingUri = null; recordLauncher.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE)) },
                        onSaveClick = { viewModel.saveVideo(videoUri, title, currentUsername) },
                        isSaving = isSaving,
                        error = error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
