package com.example.tiktokapp.data.repository

import android.util.Log
import com.example.tiktokapp.data.db.dao.VideoDao
import com.example.tiktokapp.data.mappers.toDomain
import com.example.tiktokapp.data.mappers.toEntity
import com.example.tiktokapp.data.remote.VideoRemoteDataSource
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.repository.VideoRepository

class VideoRepositoryImpl(
    private val videoDao: VideoDao,
    private val remoteDataSource: VideoRemoteDataSource = VideoRemoteDataSource()
) : VideoRepository {

    /**
     * Get all videos from local database
     */
    override suspend fun getAllVideos(): List<Video> {
        return try {
            val entities = videoDao.getAllVideos()
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error getting videos from DB", e)
            emptyList()
        }
    }

    /**
     * Get a specific video by ID from local database
     */
    override suspend fun getVideoById(id: String): Video? {
        return try {
            videoDao.getVideoById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error getting video by id from DB", e)
            null
        }
    }

    /**
     * Fetch videos from remote API and save to local database
     */
    override suspend fun fetchRemoteVideos(count: Int): List<Video> {
        return try {
            Log.d("VideoRepositoryImpl", "Fetching videos from API...")

            // Fetch from API
            val remoteDtos = remoteDataSource.fetchVideos(count)

            // Convert DTOs to entities and save to DB
            val entities = remoteDtos.map { it.toEntity() }
            videoDao.insertAll(entities)
            Log.d("VideoRepositoryImpl", "Saved ${entities.size} videos to DB")

            // Return as domain models
            remoteDtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error fetching remote videos", e)
            // If fetch fails, return data from local DB
            getAllVideos()
        }
    }

    /**
     * Refresh videos: clear local DB and fetch new ones from API
     */
    override suspend fun refreshVideos(count: Int): List<Video> {
        return try {
            videoDao.deleteAllVideos()
            fetchRemoteVideos(count)
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error refreshing videos", e)
            getAllVideos()
        }
    }
}

