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
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.models.Comment
import kotlinx.coroutines.launch
import java.util.*

class CreateVideoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseProvider.provide(application)

    // Utiliser le repository pour créer la vidéo
    private val videoRepository = VideoRepositoryImpl(
        videoDao = database.videoDao(),
        commentRepository = CommentRepositoryImpl(database.commentDao())
    )

    // current user stocké dans le ViewModel ; peut être mis à jour depuis le composable
    private var currentUser: String = "Moi"

    fun setCurrentUser(username: String) {
        if (username.isNotBlank()) currentUser = username
    }

    private val _isSaving = MutableLiveData(false)
    val isSaving: LiveData<Boolean> = _isSaving

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _saved = MutableLiveData<Boolean>(false)
    val saved: LiveData<Boolean> = _saved

    fun saveVideo(uri: Uri?, title: String, user: String) {
        if (uri == null) {
            _error.value = "Aucune vidéo sélectionnée"
            return
        }

        _isSaving.value = true
        viewModelScope.launch {
            try {
                val id = UUID.randomUUID().toString()

                // Utiliser le user passé s'il est non-blank, sinon fallback sur currentUser
                val finalUser = if (user.isNotBlank()) user else currentUser

                val video = Video(
                    id = id,
                    url = uri.toString(),
                    title = title.ifBlank { "Sans titre" },
                    user = finalUser,
                    likes = 0,
                    shares = 0,
                    reposts = 0,
                    comments = emptyList<Comment>(),
                    isLiked = false
                )

                val created = videoRepository.createVideo(video)

                if (created != null) {
                    Log.d("CreateVideoViewModel", "Saved video ${created.id} to DB via repository")
                    _saved.value = true
                } else {
                    _error.value = "Impossible de sauvegarder la vidéo"
                    _saved.value = false
                }
            } catch (e: Exception) {
                Log.e("CreateVideoViewModel", "Error saving video", e)
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
}
