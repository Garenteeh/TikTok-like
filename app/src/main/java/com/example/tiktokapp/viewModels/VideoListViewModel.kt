package com.example.tiktokapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.models.Comment
import com.example.tiktokapp.models.Video
import kotlinx.coroutines.launch

class VideoListViewModel : ViewModel() {
    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var page = 0
    private val pageSize = 10

    init {
        loadMoreVideos()
    }

    fun loadMoreVideos() {
        if (_isLoading.value == true) return
        _isLoading.value = true
        viewModelScope.launch {
            val newVideos = List(pageSize) { i ->
                val index = page * pageSize + i
                Video(
                    id = index.toString(),
                    url = "https://example.com/video$index.mp4",
                    title = "Vidéo $index",
                    user = "Utilisateur $index",
                    likes = (0..1000).random(),
                    shares = (0..100).random(),
                    reposts = (0..50).random(),
                    comments = List((0..10).random()) { j ->
                        Comment(
                            id = "$index-$j",
                            user = "Commentateur $j",
                            text = "Commentaire $j sur vidéo $index"
                        )
                    }
                )
            }
            _videos.value = _videos.value.orEmpty() + newVideos
            page++
            _isLoading.value = false
        }
    }
}

