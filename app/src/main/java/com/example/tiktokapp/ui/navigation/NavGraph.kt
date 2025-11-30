package com.example.tiktokapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiktokapp.ui.screens.HomeScreen
import com.example.tiktokapp.ui.screens.SignupScreen
import com.example.tiktokapp.viewModels.UserViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun NavGraph(
    userViewModel: UserViewModel,
    videoViewModel: VideoListViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.SIGNUP) {
        composable(Destinations.SIGNUP) { SignupScreen(
            onSignupSuccess = { navController.navigate(Destinations.HOME) },
            userViewModel= userViewModel
        ) }
        composable(Destinations.HOME) { HomeScreen() }
    }
}