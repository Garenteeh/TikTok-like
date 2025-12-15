package com.example.tiktokapp.data.remote

import android.util.Log
import com.example.tiktokapp.domain.models.dto.VideoDto
import com.example.tiktokapp.network.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

class VideoRemoteDataSource {

    suspend fun fetchVideos(count: Int): List<VideoDto> {
        return try {
            Log.d("VideoRemoteDataSource", "Fetching $count videos from API...")
            val videos = RetrofitClient.api.getVideos(count)

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
        } catch (e: HttpException) {
            Log.e("VideoRemoteDataSource", "HTTP error: ${e.code()} - ${e.message()}", e)
            emptyList()
        } catch (e: IOException) {
            Log.e("VideoRemoteDataSource", "Network error: ${e.message}", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("VideoRemoteDataSource", "Unknown error: ${e.javaClass.simpleName} - ${e.message}", e)
            emptyList()
        }
    }
}

