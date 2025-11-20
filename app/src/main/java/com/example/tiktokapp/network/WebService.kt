package com.example.tiktokapp.network

import com.example.tiktokapp.models.Video
import retrofit2.http.GET

interface WebService {
    @GET("videos.json")
    suspend fun getVideos(): List<Video>
}