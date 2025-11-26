package com.example.tiktokapp.domain.models


data class Video(
    val id: String,
    val url: String,
    val title: String,
    val user: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val reposts: Int = 0,
    val comments: List<Comment>? = emptyList(),
    val isLiked: Boolean = false
) {
    fun totalCommentsCount(): Int {
        fun count(commentList: List<Comment>?): Int {
            if (commentList == null) return 0
            return commentList.sumOf { 1 + count(it.replies) }
        }
        return count(comments)
    }

    /**
     * Format number for display (1k, 10.1k, 1M, etc.)
     */
    fun formatCount(count: Int): String {
        return when {
            count < 1000 -> count.toString()
            count < 10000 -> String.format("%.1fk", count / 1000.0)
            count < 1000000 -> "${count / 1000}k"
            count < 10000000 -> String.format("%.1fM", count / 1000000.0)
            else -> "${count / 1000000}M"
        }
    }
}

