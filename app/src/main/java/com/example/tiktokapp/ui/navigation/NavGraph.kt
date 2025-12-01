package com.example.tiktokapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiktokapp.ui.screens.HomeScreen
import com.example.tiktokapp.ui.screens.SignupScreen
import com.example.tiktokapp.ui.screens.LoginScreen
import com.example.tiktokapp.viewModels.LoginViewModel
import com.example.tiktokapp.viewModels.RegisterViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun NavGraph(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    videoViewModel: VideoListViewModel,
) {
    val navController = rememberNavController()

    // Déterminer la destination de départ directement depuis currentUser
    val currentUser by loginViewModel.currentUser.collectAsState()

    val startDestination = if (currentUser != null) Destinations.HOME else Destinations.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destinations.SIGNUP) {
            SignupScreen(
                registerViewModel = registerViewModel,
                onNavigateToLogin = { navController.navigate(Destinations.LOGIN) },
                onSignupSucess = { navController.navigate(Destinations.HOME) }
            )
        }
        composable(Destinations.LOGIN) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSucess = { navController.navigate(Destinations.HOME) },
                onNavigateToSignup = { navController.navigate(Destinations.SIGNUP) }
            )
        }
        composable(Destinations.HOME) { HomeScreen() }
    }
}