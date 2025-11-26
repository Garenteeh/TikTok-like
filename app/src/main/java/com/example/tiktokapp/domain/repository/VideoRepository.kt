package com.example.tiktokapp.domain.repository

import com.example.tiktokapp.domain.models.Video

/**
 * Domain-level repository interface for videos.
 * Define the contract here so the app depends on the interface (clean architecture).
 */
interface VideoRepository {
    suspend fun getAllVideos(): List<Video>
    suspend fun getVideoById(id: String): Video?
    suspend fun fetchRemoteVideos(count: Int): List<Video>
    suspend fun refreshVideos(count: Int): List<Video>
}

