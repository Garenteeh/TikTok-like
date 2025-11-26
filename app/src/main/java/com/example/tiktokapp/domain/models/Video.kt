package com.example.tiktokapp.domain.models


data class Video(
    val id: String,
    val url: String,
    val title: String,
    val user: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val reposts: Int = 0,
    val comments: List<Comment>? = emptyList()
) {
    fun totalCommentsCount(): Int {
        fun count(commentList: List<Comment>?): Int {
            if (commentList == null) return 0
            return commentList.sumOf { 1 + count(it.replies) }
        }
        return count(comments)
    }
}

