package com.example.tiktokapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tiktokapp.ui.screens.HomeScreen
import com.example.tiktokapp.ui.screens.SignupScreen
import com.example.tiktokapp.ui.screens.LoginScreen
import com.example.tiktokapp.ui.screens.ProfileScreen
import com.example.tiktokapp.ui.screens.ConversationsScreen
import com.example.tiktokapp.ui.screens.ChatScreen
import com.example.tiktokapp.ui.screens.NewConversationScreen
import com.example.tiktokapp.viewModels.LoginViewModel
import com.example.tiktokapp.viewModels.MessagingViewModel
import com.example.tiktokapp.viewModels.RegisterViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel
import com.example.tiktokapp.ui.screens.CreateVideoScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    videoViewModel: VideoListViewModel,
) {
    val navController = rememberNavController()

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
                onNavigateToAddVideo = { navController.navigate(Destinations.CREATE_VIDEO) },
                onNavigateToProfile = {navController.navigate(Destinations.PROFILE)},
                onNavigateToMessages = {navController.navigate(Destinations.CONVERSATIONS)},
                currentUsername = currentUser?.username ?: "Moi"
            )
        }
        composable(Destinations.PROFILE) {
            ProfileScreen(
                user = currentUser,
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                onHome = { navController.navigate(Destinations.HOME) {
                    popUpTo(Destinations.PROFILE) { inclusive = true }
                } },
                onAdd = { navController.navigate(Destinations.CREATE_VIDEO) },
                onMessages = { navController.navigate(Destinations.CONVERSATIONS) }
            )
        }

        composable(Destinations.CREATE_VIDEO) {
            CreateVideoScreen(
                currentUsername = currentUser?.username ?: "Moi",
                viewModel = videoViewModel,
                onSaved = {
                navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                onNavigateHome = { navController.navigate(Destinations.CREATE_VIDEO) },
                onNavigateToProfile = {navController.navigate(Destinations.PROFILE)},
                onNavigateToMessages = {navController.navigate(Destinations.CONVERSATIONS)}
            )

        }

        // Routes de messagerie
        composable(Destinations.CONVERSATIONS) {
            val messagingViewModel: MessagingViewModel = viewModel()
            ConversationsScreen(
                onNavigateToChat = { conversationId ->
                    navController.navigate(Destinations.chat(conversationId))
                },
                onNavigateToNewConversation = {
                    navController.navigate(Destinations.NEW_CONVERSATION)
                },
                onNavigateToHome = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.CONVERSATIONS) { inclusive = true }
                    }
                },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) },
                onNavigateToAddVideo = { navController.navigate(Destinations.CREATE_VIDEO) },
                viewModel = messagingViewModel
            )
        }

        composable(
            route = Destinations.CHAT,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val messagingViewModel: MessagingViewModel = viewModel()
            ChatScreen(
                conversationId = conversationId,
                currentUsername = currentUser?.username ?: "Moi",
                onNavigateBack = { navController.popBackStack() },
                viewModel = messagingViewModel
            )
        }

        composable(Destinations.NEW_CONVERSATION) {
            val messagingViewModel: MessagingViewModel = viewModel()
            NewConversationScreen(
                currentUsername = currentUser?.username ?: "Moi",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChat = { conversationId ->
                    navController.navigate(Destinations.chat(conversationId)) {
                        popUpTo(Destinations.CONVERSATIONS) { inclusive = false }
                    }
                },
                viewModel = messagingViewModel
            )
        }
    }

}