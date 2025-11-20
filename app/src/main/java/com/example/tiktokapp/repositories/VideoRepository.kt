package com.example.tiktokapp.repositories

import android.util.Log
import com.example.tiktokapp.models.Video
import com.example.tiktokapp.network.RetrofitClient

class VideoRepository {

    suspend fun getRemoteVideos(count: Int): List<Video> {
        return try {
            Log.d("VideoRepository", "Fetching videos from API...")
            val videos = RetrofitClient.api.getVideos(count)
            Log.d("VideoRepository", "Successfully fetched ${videos.size} videos")
            videos.forEach { video ->
                Log.d("VideoRepository", "Video: id=${video.id}, title=${video.title}, url=${video.url}")
            }
            videos
        } catch (e: Exception) {
            Log.e("VideoRepository", "Error fetching videos", e)
            emptyList()
        }
    }
}
