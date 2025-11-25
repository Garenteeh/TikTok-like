package com.example.tiktokapp.network

import com.example.tiktokapp.domain.models.dto.VideoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    @GET("videos")
    suspend fun getVideos(
        @Query("count") count: Int = 50
    ): List<VideoDto>
}