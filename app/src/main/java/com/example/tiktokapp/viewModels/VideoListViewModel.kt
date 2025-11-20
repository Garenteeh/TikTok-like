package com.example.tiktokapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.models.Video
import com.example.tiktokapp.repositories.VideoRepository
import kotlinx.coroutines.launch

class VideoListViewModel(
    private val repository: VideoRepository = VideoRepository()
) : ViewModel() {

    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        Log.d("VideoListViewModel", "ViewModel initialized")
        loadMoreVideos()
    }

    fun loadMoreVideos() {
        if (_isLoading.value == true) {
            Log.d("VideoListViewModel", "Already loading, skipping...")
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val newVideos = repository.getRemoteVideos(50)

                _videos.value = _videos.value.orEmpty() + newVideos
                _isLoading.value = false

            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error loading videos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
