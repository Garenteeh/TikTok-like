package com.example.tiktokapp.ui.navigation

object Destinations {
    const val SIGNUP = "signup"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val HOME = "home"
    const val CREATE_VIDEO = "create_video"
    const val FAKE_HOME = "fake_home"
    const val CONVERSATIONS = "conversations"
    const val CHAT = "chat/{conversationId}"
    const val NEW_CONVERSATION = "new_conversation"

    fun chat(conversationId: String) = "chat/$conversationId"
}