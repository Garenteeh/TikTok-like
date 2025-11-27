package com.example.tiktokapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.repository.CommentRepositoryImpl
import com.example.tiktokapp.data.repository.VideoRepositoryImpl
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.repository.CommentRepository
import com.example.tiktokapp.domain.repository.VideoRepository
import kotlinx.coroutines.launch

class VideoListViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = DatabaseProvider.provide(application)

    private val videoRepository: VideoRepository = VideoRepositoryImpl(
        videoDao = database.videoDao(),
        commentRepository = CommentRepositoryImpl(database.commentDao())
    )

    private val commentRepository: CommentRepository = CommentRepositoryImpl(
        commentDao = database.commentDao()
    )

    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        Log.d("VideoListViewModel", "ViewModel initialized")
        loadVideosFromDb()
        loadMoreVideos()
    }

    /**
     * Load videos from local database first
     */
    private fun loadVideosFromDb() {
        viewModelScope.launch {
            try {
                val localVideos = videoRepository.getAllVideos()
                if (localVideos.isNotEmpty()) {
                    _videos.value = localVideos
                    Log.d("VideoListViewModel", "Loaded ${localVideos.size} videos from DB")
                }
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error loading videos from DB", e)
            }
        }
    }

    /**
     * Load more videos from remote API and save to DB
     */
    fun loadMoreVideos() {
        if (_isLoading.value == true) {
            Log.d("VideoListViewModel", "Already loading, skipping...")
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val newVideos = videoRepository.fetchRemoteVideos(50)
                _videos.value = newVideos
                Log.d("VideoListViewModel", "Loaded ${newVideos.size} new videos")
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error loading videos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh videos - clear DB and fetch new ones
     */
    fun refreshVideos() {
        if (_isLoading.value == true) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val videos = videoRepository.refreshVideos(50)
                _videos.value = videos
                Log.d("VideoListViewModel", "Refreshed with ${videos.size} videos")
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error refreshing videos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggle like status for a video
     */
    fun toggleLike(videoId: String) {
        viewModelScope.launch {
            try {
                val updatedVideo = videoRepository.toggleLike(videoId)
                if (updatedVideo != null) {
                    // Update only likes and isLiked fields, preserve comments from original video
                    _videos.value = _videos.value?.map { video ->
                        if (video.id == videoId) {
                            video.copy(
                                likes = updatedVideo.likes,
                                isLiked = updatedVideo.isLiked,
                                comments = video.comments // Preserve original comments
                            )
                        } else {
                            video
                        }
                    }
                    Log.d("VideoListViewModel", "Toggled like for video $videoId")
                }
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error toggling like", e)
            }
        }
    }

    /**
     * Toggle like status for a comment
     */
    fun toggleCommentLike(videoId: String, commentId: String) {
        Log.d("VideoListViewModel", "toggleCommentLike called: videoId=$videoId, commentId=$commentId")

        viewModelScope.launch {
            // Trouver le commentaire et mettre à jour ses likes
            var updatedComment: com.example.tiktokapp.domain.models.Comment? = null

            _videos.value = _videos.value?.map { video ->
                if (video.id == videoId) {
                    Log.d("VideoListViewModel", "Found video, updating comments")
                    video.copy(comments = toggleCommentLikeRecursive(video.comments, commentId) { comment ->
                        updatedComment = comment
                    })
                } else {
                    video
                }
            }

            // Sauvegarder en DB via CommentRepository
            updatedComment?.let { comment ->
                commentRepository.updateCommentLike(comment.id, comment.likes, comment.isLiked)
            }

            Log.d("VideoListViewModel", "Videos updated: ${_videos.value?.size} videos")
        }
    }

    /**
     * Recursive function to toggle like on a comment or its replies
     */
    private fun toggleCommentLikeRecursive(
        comments: List<com.example.tiktokapp.domain.models.Comment>?,
        commentId: String,
        onFound: (com.example.tiktokapp.domain.models.Comment) -> Unit = {}
    ): List<com.example.tiktokapp.domain.models.Comment>? {
        return comments?.map { comment ->
            if (comment.id == commentId) {
                // Toggle like on this comment
                Log.d("VideoListViewModel", "Found comment $commentId, toggling like from ${comment.isLiked} to ${!comment.isLiked}")
                val updated = comment.copy(
                    isLiked = !comment.isLiked,
                    likes = if (comment.isLiked) comment.likes - 1 else comment.likes + 1
                )
                onFound(updated)
                updated
            } else {
                // Check replies recursively
                comment.copy(replies = toggleCommentLikeRecursive(comment.replies, commentId, onFound))
            }
        }
    }

    /**
     * Add a new comment to a video
     */
    fun addComment(videoId: String, message: String, username: String = "Moi") {
        Log.d("VideoListViewModel", "addComment called: videoId=$videoId, message=$message")

        viewModelScope.launch {
            val newComment = com.example.tiktokapp.domain.models.Comment(
                id = java.util.UUID.randomUUID().toString(),
                message = message,
                user = username,
                timestamp = System.currentTimeMillis(),
                likes = 0,
                isLiked = false,
                replies = emptyList()
            )

            // Mettre à jour l'UI
            _videos.value = _videos.value?.map { video ->
                if (video.id == videoId) {
                    val updatedComments = (video.comments ?: emptyList()) + newComment
                    Log.d("VideoListViewModel", "Comment added, total comments: ${updatedComments.size}")
                    video.copy(comments = updatedComments)
                } else {
                    video
                }
            }

            // Sauvegarder en DB via CommentRepository
            commentRepository.saveComment(videoId, newComment)
        }
    }

    /**
     * Add a reply to a comment
     */
    fun addReplyToComment(videoId: String, commentId: String, message: String, username: String = "Moi") {
        Log.d("VideoListViewModel", "addReplyToComment called: commentId=$commentId, message=$message")

        viewModelScope.launch {
            val newReply = com.example.tiktokapp.domain.models.Comment(
                id = java.util.UUID.randomUUID().toString(),
                message = message,
                user = username,
                timestamp = System.currentTimeMillis(),
                likes = 0,
                isLiked = false,
                replies = emptyList()
            )

            // Mettre à jour l'UI
            _videos.value = _videos.value?.map { video ->
                if (video.id == videoId) {
                    video.copy(comments = addReplyRecursive(video.comments, commentId, newReply))
                } else {
                    video
                }
            }

            // Sauvegarder en DB via CommentRepository
            commentRepository.saveComment(videoId, newReply, commentId)
        }
    }

    /**
     * Recursive function to add a reply to a comment
     */
    private fun addReplyRecursive(
        comments: List<com.example.tiktokapp.domain.models.Comment>?,
        commentId: String,
        newReply: com.example.tiktokapp.domain.models.Comment
    ): List<com.example.tiktokapp.domain.models.Comment>? {
        return comments?.map { comment ->
            if (comment.id == commentId) {
                // Add reply to this comment
                val updatedReplies = (comment.replies ?: emptyList()) + newReply
                Log.d("VideoListViewModel", "Reply added to comment $commentId, total replies: ${updatedReplies.size}")
                comment.copy(replies = updatedReplies)
            } else {
                // Check replies recursively
                comment.copy(replies = addReplyRecursive(comment.replies, commentId, newReply))
            }
        }
    }
}
