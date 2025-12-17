package com.example.tiktokapp.ui.navigation

object Destinations {
    const val SIGNUP = "signup"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val HOME = "home"
    const val CREATE_VIDEO = "create_video"
    const val CONVERSATIONS = "conversations"
    const val CHAT = "chat/{conversationId}"
    const val SHARE_VIDEO_ROUTES = "share_video?video={video}"
    const val NEW_CONVERSATION = "new_conversation"

    fun chat(conversationId: String) = "chat/$conversationId"
    fun shareVideo(video: String?): String {
        val encoded = video ?: ""
        return "share_video?video=$encoded"
    }
}