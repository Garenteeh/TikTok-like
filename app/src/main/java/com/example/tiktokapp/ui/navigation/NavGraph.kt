package com.example.tiktokapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiktokapp.ui.screens.HomeScreen
import com.example.tiktokapp.ui.screens.SignupScreen
import com.example.tiktokapp.ui.screens.LoginScreen
import com.example.tiktokapp.viewModels.UserViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun NavGraph(
    userViewModel: UserViewModel,
    videoViewModel: VideoListViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.SIGNUP) {
        composable(Destinations.SIGNUP) {
            SignupScreen(
                userViewModel = userViewModel,
                onNavigateToLogin = { navController.navigate(Destinations.LOGIN) },
                onSignupSucess = { navController.navigate(Destinations.HOME) }
            )
        }
        composable(Destinations.LOGIN) {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSucess = { navController.navigate(Destinations.HOME) },
                onNavigateToSignup = { navController.navigate(Destinations.SIGNUP) }
            )
        }
        composable(Destinations.HOME) { HomeScreen() }
    }
}