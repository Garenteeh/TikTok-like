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

    private var allVideos: List<Video> = emptyList()
    private var page = 0
    private val pageSize = 5

    init {
        Log.d("VideoListViewModel", "ViewModel initialized")
        loadMoreVideos()
    }

    fun loadMoreVideos() {
        if (_isLoading.value == true) {
            Log.d("VideoListViewModel", "Already loading, skipping...")
            return
        }

        Log.d("VideoListViewModel", "Loading more videos - page: $page")
        _isLoading.value = true

        viewModelScope.launch {
            try {
                if (allVideos.isEmpty()) {
                    Log.d("VideoListViewModel", "Fetching all videos from repository...")
                    allVideos = repository.getRemoteVideos()
                    Log.d("VideoListViewModel", "Total videos fetched: ${allVideos.size}")
                }

                val start = page * pageSize
                val end = (start + pageSize).coerceAtMost(allVideos.size)
                val newPage = if (start < allVideos.size) allVideos.subList(start, end) else emptyList()

                Log.d("VideoListViewModel", "Loading videos from index $start to $end (${newPage.size} videos)")

                _videos.value = _videos.value.orEmpty() + newPage
                Log.d("VideoListViewModel", "Total videos in list: ${_videos.value?.size}")

                page++
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error loading videos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
