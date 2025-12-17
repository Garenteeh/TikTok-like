package com.example.tiktokapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.net.Uri
import com.example.tiktokapp.ui.screens.ChatScreen
import com.example.tiktokapp.ui.screens.ConversationsScreen
import com.example.tiktokapp.ui.screens.CreateVideoScreen
import com.example.tiktokapp.ui.screens.HomeScreen
import com.example.tiktokapp.ui.screens.LoginScreen
import com.example.tiktokapp.ui.screens.NewConversationScreen
import com.example.tiktokapp.ui.screens.ProfileScreen
import com.example.tiktokapp.ui.screens.ShareScreen
import com.example.tiktokapp.ui.screens.SignupScreen
import com.example.tiktokapp.viewModels.MessagingViewModel
import com.example.tiktokapp.viewModels.UserViewModel
import com.example.tiktokapp.viewModels.VideoListViewModel

@Composable
fun NavGraph(
    userViewModel: UserViewModel,
    videoViewModel: VideoListViewModel,
) {
    val navController = rememberNavController()

    val currentUser by userViewModel.currentUser.collectAsState()

    val startDestination = if (currentUser != null) Destinations.HOME else Destinations.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destinations.SIGNUP) {
            SignupScreen(
                userViewModel = userViewModel,
                onNavigateToLogin = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(Destinations.SIGNUP) { inclusive = true }
                    }
                },
                onSignupSucess = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SIGNUP) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.LOGIN) {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSucess = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate(Destinations.SIGNUP) }
            )
        }
        composable(Destinations.HOME) {
            HomeScreen(
                viewModel = videoViewModel,
                onNavigateToAddVideo = { navController.navigate(Destinations.CREATE_VIDEO) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) },
                onNavigateToMessages = { navController.navigate(Destinations.CONVERSATIONS) },
                onShareVideo = { videoUrl ->
                    // Encode the video URL safely for navigation (videoUrl is non-null)
                    val encoded = if (videoUrl.isBlank()) "" else Uri.encode(videoUrl)
                    navController.navigate(Destinations.shareVideo(encoded))
                },
                currentUsername = currentUser?.username ?: "Moi"
            )
        }
        composable(Destinations.PROFILE) {
            ProfileScreen(
                userViewModel = userViewModel,
                onHome = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.PROFILE) { inclusive = true }
                    }
                },
                onAdd = { navController.navigate(Destinations.CREATE_VIDEO) },
                onMessages = { navController.navigate(Destinations.CONVERSATIONS) },
                onLogout = { navController.navigate(Destinations.LOGIN) }
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
                onNavigateHome = { navController.navigate(Destinations.HOME) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) },
                onNavigateToMessages = { navController.navigate(Destinations.CONVERSATIONS) }
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

        // New conversation route with optional shared video param -> use ShareScreen
        composable(
            route = Destinations.SHARE_VIDEO_ROUTES,
            arguments = listOf(navArgument("video") {
                type = NavType.StringType; nullable = true; defaultValue = ""
            })
        ) { backStackEntry ->
            val messagingViewModel: MessagingViewModel = viewModel()
            val sharedVideo = backStackEntry.arguments?.getString("video")
            ShareScreen(
                currentUser?.username ?: "Moi",
                sharedVideo,
                { navController.popBackStack() },
                { conversationId ->
                    navController.navigate(Destinations.chat(conversationId)) {
                        popUpTo(Destinations.CONVERSATIONS) { inclusive = false }
                    }
                },
                viewModel = messagingViewModel
            )
        }

        // Backwards-compatible route: some parts of the app may navigate to "share_video?video=..."
        composable(
            route = "share_video?video={video}",
            arguments = listOf(navArgument("video") {
                type = NavType.StringType; nullable = true; defaultValue = ""
            })
        ) { backStackEntry ->
            val messagingViewModel: MessagingViewModel = viewModel()
            val sharedVideo = backStackEntry.arguments?.getString("video")
            ShareScreen(
                currentUser?.username ?: "Moi",
                sharedVideo,
                { navController.popBackStack() },
                { conversationId ->
                    navController.navigate(Destinations.chat(conversationId)) {
                        popUpTo(Destinations.CONVERSATIONS) { inclusive = false }
                    }
                },
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
                onNavigateBack = { navController.navigate(Destinations.CONVERSATIONS) },
                viewModel = messagingViewModel
            )
        }

        composable(Destinations.NEW_CONVERSATION) {
            val messagingViewModel: MessagingViewModel = viewModel()
            NewConversationScreen(
                currentUser?.username ?: "Moi",
                { navController.navigate(Destinations.CONVERSATIONS) },
                { conversationId ->
                    navController.navigate(Destinations.chat(conversationId)) {
                        popUpTo(Destinations.CONVERSATIONS) { inclusive = false }
                    }
                },
                messagingViewModel
            )
        }
    }

}