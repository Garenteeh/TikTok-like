package com.example.tiktokapp.data.remote

import android.util.Log
import com.example.tiktokapp.domain.models.dto.VideoDto
import com.example.tiktokapp.network.RetrofitClient

class VideoRemoteDataSource {

    suspend fun fetchVideos(count: Int): List<VideoDto> {
        return try {
            Log.d("VideoRemoteDataSource", "Fetching videos from API...")
            val videos = RetrofitClient.api.getVideos(count)
            Log.d("VideoRemoteDataSource", "Successfully fetched ${videos.size} videos")

            videos.map { video ->
                VideoDto(
                    id = video.id,
                    url = video.url,
                    title = video.title,
                    user = video.user,
                    likes = video.likes,
                    shares = video.shares,
                    reposts = video.reposts,
                    comments = video.comments
                )
            }
        } catch (e: Exception) {
            Log.e("VideoRemoteDataSource", "Error fetching videos", e)
            emptyList()
        }
    }
}

