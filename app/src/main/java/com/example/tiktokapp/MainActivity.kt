package com.example.tiktokapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.tiktokapp.ui.navigation.NavGraph
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import com.example.tiktokapp.viewModels.UserViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        enableEdgeToEdge()
        setContent {
            TikTokAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val videoViewModel = VideoListViewModel(
                        application = application
                    )
                    val userViewModel = UserViewModel(
                        application = application
                    )
                    NavGraph(userViewModel, videoViewModel)
                }
            }
        }
    }
}
