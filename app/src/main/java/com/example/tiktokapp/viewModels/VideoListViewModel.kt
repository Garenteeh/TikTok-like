package com.example.tiktokapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktokapp.models.Video
import com.example.tiktokapp.repositories.VideoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoListViewModel(
    private val repository: VideoRepository = VideoRepository()
) : ViewModel() {

    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var page = 0
    private val pageSize = 5

    init {
        loadMoreVideos()
    }

    fun loadMoreVideos() {
        if (_isLoading.value == true) return
        _isLoading.value = true

        viewModelScope.launch {
            delay(800)

            val allVideos = repository.getVideos()

            val start = page * pageSize
            val end = (start + pageSize).coerceAtMost(allVideos.size)

            val newVideos = if (start < allVideos.size) {
                allVideos.subList(start, end)
            } else {
                emptyList()
            }

            _videos.value = _videos.value.orEmpty() + newVideos
            page++
            _isLoading.value = false
        }
    }
}
