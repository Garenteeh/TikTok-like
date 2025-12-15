package com.example.tiktokapp.viewModels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.data.db.provider.DatabaseProvider
import com.example.tiktokapp.data.repository.CommentRepositoryImpl
import com.example.tiktokapp.data.repository.VideoRepositoryImpl
import com.example.tiktokapp.domain.models.Comment
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.repository.CommentRepository
import com.example.tiktokapp.domain.repository.VideoRepository
import kotlinx.coroutines.launch
import java.util.*

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


    private val _isSaving = MutableLiveData(false)
    val isSaving: LiveData<Boolean> = _isSaving

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _saved = MutableLiveData<Boolean>(false)
    val saved: LiveData<Boolean> = _saved

    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        Log.d("VideoListViewModel", "ViewModel initialized")
        loadVideosFromDb()
        loadMoreVideos()
    }

    fun saveVideo(uri: Uri?, title: String, user: String) {
        if (uri == null) {
            _error.value = "Aucune vidéo sélectionnée"
            return
        }

        _isSaving.value = true

        viewModelScope.launch {
            try {
                val video = Video(
                    id = UUID.randomUUID().toString(),
                    url = uri.toString(),
                    title = title.ifBlank { "Sans titre" },
                    user = user,
                    likes = 0,
                    shares = 0,
                    reposts = 0,
                    comments = emptyList<Comment>(),
                    isLiked = false
                )

                val created = videoRepository.createVideo(video)

                if (created != null) {
                    // ajouter la vidéo nouvellement créée en tête de la liste observable
                    val current = _videos.value ?: emptyList()
                    _videos.value = listOf(created) + current
                    _saved.value = true
                } else {
                    _error.value = "Impossible de sauvegarder la vidéo"
                    _saved.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de la sauvegarde"
                _saved.value = false
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearSavedFlag() {
        _saved.value = false
    }
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



    fun toggleLike(videoId: String) {
        viewModelScope.launch {
            try {
                val updatedVideo = videoRepository.toggleLike(videoId)
                if (updatedVideo != null) {
                    _videos.value = _videos.value?.map { video ->
                        if (video.id == videoId) {
                            video.copy(
                                likes = updatedVideo.likes,
                                isLiked = updatedVideo.isLiked,
                                comments = video.comments
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

    fun toggleCommentLike(videoId: String, commentId: String) {
        Log.d("VideoListViewModel", "toggleCommentLike called: videoId=$videoId, commentId=$commentId")

        viewModelScope.launch {
            var updatedComment: Comment? = null

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

            updatedComment?.let { comment ->
                commentRepository.updateCommentLike(comment.id, comment.likes, comment.isLiked)
            }

            Log.d("VideoListViewModel", "Videos updated: ${_videos.value?.size} videos")
        }
    }


    private fun toggleCommentLikeRecursive(
        comments: List<Comment>?,
        commentId: String,
        onFound: (Comment) -> Unit = {}
    ): List<Comment>? {
        return comments?.map { comment ->
            if (comment.id == commentId) {
                Log.d("VideoListViewModel", "Found comment $commentId, toggling like from ${comment.isLiked} to ${!comment.isLiked}")
                val updated = comment.copy(
                    isLiked = !comment.isLiked,
                    likes = if (comment.isLiked) comment.likes - 1 else comment.likes + 1
                )
                onFound(updated)
                updated
            } else {
                comment.copy(replies = toggleCommentLikeRecursive(comment.replies, commentId, onFound))
            }
        }
    }

    fun addComment(videoId: String, message: String, username: String) {
        Log.d("VideoListViewModel", "addComment called: videoId=$videoId, message=$message")

        viewModelScope.launch {
            val newComment = Comment(
                id = UUID.randomUUID().toString(),
                message = message,
                user = username,
                timestamp = System.currentTimeMillis(),
                likes = 0,
                isLiked = false,
                replies = emptyList()
            )

            _videos.value = _videos.value?.map { video ->
                if (video.id == videoId) {
                    val updatedComments = (video.comments ?: emptyList()) + newComment
                    Log.d("VideoListViewModel", "Comment added, total comments: ${updatedComments.size}")
                    video.copy(comments = updatedComments)
                } else {
                    video
                }
            }

            commentRepository.saveComment(videoId, newComment)
        }
    }

    fun addReplyToComment(videoId: String, commentId: String, message: String, username: String) {
        Log.d("VideoListViewModel", "addReplyToComment called: commentId=$commentId, message=$message")

        viewModelScope.launch {
            val newReply = Comment(
                id = UUID.randomUUID().toString(),
                message = message,
                user = username,
                timestamp = System.currentTimeMillis(),
                likes = 0,
                isLiked = false,
                replies = emptyList()
            )

            _videos.value = _videos.value?.map { video ->
                if (video.id == videoId) {
                    video.copy(comments = addReplyRecursive(video.comments, commentId, newReply))
                } else {
                    video
                }
            }

            commentRepository.saveComment(videoId, newReply, commentId)
        }
    }

    private fun addReplyRecursive(
        comments: List<Comment>?,
        commentId: String,
        newReply: Comment
    ): List<Comment>? {
        return comments?.map { comment ->
            if (comment.id == commentId) {
                val updatedReplies = (comment.replies ?: emptyList()) + newReply
                Log.d("VideoListViewModel", "Reply added to comment $commentId, total replies: ${updatedReplies.size}")
                comment.copy(replies = updatedReplies)
            } else {
                comment.copy(replies = addReplyRecursive(comment.replies, commentId, newReply))
            }
        }
    }

    fun deleteComment(videoId: String, commentId: String, currentUsername: String) {
        Log.d("VideoListViewModel", "deleteComment called: videoId=$videoId, commentId=$commentId")

        viewModelScope.launch {
            _videos.value = _videos.value?.map { video ->
                if (video.id == videoId) {
                    video.copy(comments = deleteCommentRecursive(video.comments, commentId, currentUsername, video.user))
                } else {
                    video
                }
            }

            commentRepository.deleteComment(commentId)
        }
    }


    private fun deleteCommentRecursive(
        comments: List<Comment>?,
        commentId: String,
        currentUsername: String,
        videoOwner: String
    ): List<Comment>? {
        return comments?.mapNotNull { comment ->
            if (comment.id == commentId) {
                if (comment.user == currentUsername || videoOwner == currentUsername) {
                    Log.d("VideoListViewModel", "Comment $commentId deleted")
                    null
                } else {
                    Log.d("VideoListViewModel", "Cannot delete comment $commentId: not authorized")
                    comment
                }
            } else {
                comment.copy(replies = deleteCommentRecursive(comment.replies, commentId, currentUsername, videoOwner))
            }
        }
    }
}
