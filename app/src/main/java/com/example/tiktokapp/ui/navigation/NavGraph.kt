package com.example.tiktokapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiktokapp.ui.screens.HomeScreen
import com.example.tiktokapp.ui.screens.SignupScreen
import com.example.tiktokapp.ui.screens.LoginScreen
import com.example.tiktokapp.ui.screens.ProfileScreen
import com.example.tiktokapp.viewModels.LoginViewModel
import com.example.tiktokapp.viewModels.RegisterViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel
import kotlinx.coroutines.launch

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
                onNavigateToLogin = { navController.navigate(Destinations.LOGIN) {
                    popUpTo(Destinations.SIGNUP) { inclusive = true }
                } },
                onSignupSucess = { navController.navigate(Destinations.HOME) {
                    popUpTo(Destinations.SIGNUP) { inclusive = true }
                } }
            )
        }
        composable(Destinations.LOGIN) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSucess = { navController.navigate(Destinations.HOME) {
                    popUpTo(Destinations.LOGIN) { inclusive = true }
                } },
                onNavigateToSignup = { navController.navigate(Destinations.SIGNUP) }
            )
        }
        composable(Destinations.HOME) {
            HomeScreen(
                viewModel = videoViewModel,
                onNavigateToAddVideo = {/* TODO à implementer*/},
                onNavigateToProfile = {navController.navigate(Destinations.PROFILE)}
            )
        }
        composable(Destinations.PROFILE) {
            ProfileScreen(
                user = loginViewModel.currentUser.value,
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                onHome = { navController.navigate(Destinations.HOME) {
                    popUpTo(Destinations.PROFILE) { inclusive = true }
                } },
                onAdd = { navController.navigate(Destinations.HOME) }
            )
        }
    }

}