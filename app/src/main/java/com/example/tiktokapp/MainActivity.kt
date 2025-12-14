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
import androidx.lifecycle.ViewModelProvider
import com.example.tiktokapp.data.datasource.TokenLocalDataSource
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.repository.AuthRepositoryImpl
import com.example.tiktokapp.domain.usecases.LoginUseCase
import com.example.tiktokapp.domain.usecases.RegisterUseCase
import com.example.tiktokapp.ui.navigation.NavGraph
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import com.example.tiktokapp.viewModels.AuthViewModelFactory
import com.example.tiktokapp.viewModels.LoginViewModel
import com.example.tiktokapp.viewModels.RegisterViewModel
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
                    val db = DatabaseProvider.provide(application)
                    val tokenLocal = TokenLocalDataSource(application)
                    val authRepo = AuthRepositoryImpl(db.userDao(), tokenLocal)

                    val loginUseCase = LoginUseCase(authRepo)
                    val registerUseCase = RegisterUseCase(authRepo)
                    val factory = AuthViewModelFactory(application, loginUseCase, registerUseCase)
                    val provider = ViewModelProvider(this, factory)
                    val loginViewModel = provider[LoginViewModel::class.java]
                    val registerViewModel = provider[RegisterViewModel::class.java]

                    NavGraph(loginViewModel, registerViewModel, videoViewModel)
                }
            }
        }
    }
}
