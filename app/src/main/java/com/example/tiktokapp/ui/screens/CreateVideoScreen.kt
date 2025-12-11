package com.example.tiktokapp.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiktokapp.ui.components.BottomBar
import com.example.tiktokapp.viewModels.CreateVideoViewModel
import com.example.tiktokapp.ui.components.VideoPlayer

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateVideoScreen(
    viewModel: CreateVideoViewModel = viewModel(),
    currentUsername: String = "Moi",
    onSaved: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current

    val isSaving by viewModel.isSaving.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val saved by viewModel.saved.observeAsState(false)

    var title by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            videoUri = uri
        }
    )

    val recordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val dataUri: Uri? = result.data?.data
            videoUri = dataUri
        }
    }

    LaunchedEffect(saved) {
        if (saved) {
            Toast.makeText(context, "Vidéo sauvegardée", Toast.LENGTH_SHORT).show()
            viewModel.clearSavedFlag()
            onSaved()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setCurrentUser(currentUsername)
    }
    Scaffold(
        bottomBar = {
            BottomBar(
                onHome = onNavigateHome,
                onProfile = onNavigateToProfile,
                onAdd = {/* current */}
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (videoUri != null) {
                // Utiliser VideoPlayer composable existant pour preview
                VideoPlayer(
                    videoUrl = videoUri.toString(),
                    isPlaying = true,
                    modifier = Modifier.height(300.dp).fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Aucune vidéo sélectionnée")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Removed user text field. Use currentUsername parameter instead.

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { pickVideoLauncher.launch("video/*") }) {
                    Text("Choisir une vidéo")
                }

                Button(onClick = {
                    // Lancer l'app caméra système pour enregistrer une vidéo
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    recordLauncher.launch(intent)
                }) {
                    Text("Enregistrer (caméra)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSaving) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.saveVideo(videoUri, title, currentUsername)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sauvegarder")
                }
            }

            error?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = err)
            }
        }
    }
}
