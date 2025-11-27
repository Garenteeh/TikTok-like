package com.example.tiktokapp.data.repository

import android.util.Log
import com.example.tiktokapp.data.db.dao.VideoDao
import com.example.tiktokapp.data.mappers.toDomain
import com.example.tiktokapp.data.mappers.toEntity
import com.example.tiktokapp.data.remote.VideoRemoteDataSource
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.repository.CommentRepository
import com.example.tiktokapp.domain.repository.VideoRepository

class VideoRepositoryImpl(
    private val videoDao: VideoDao,
    private val commentRepository: CommentRepository,
    private val remoteDataSource: VideoRemoteDataSource = VideoRemoteDataSource()
) : VideoRepository {

    /**
     * Get all videos from local database with their comments
     */
    override suspend fun getAllVideos(): List<Video> {
        return try {
            val entities = videoDao.getAllVideos()
            entities.map { videoEntity ->
                val comments = commentRepository.getCommentsForVideo(videoEntity.id)
                videoEntity.toDomain().copy(comments = comments)
            }
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error getting videos from DB", e)
            emptyList()
        }
    }

    /**
     * Get a specific video by ID from local database with comments
     */
    override suspend fun getVideoById(id: String): Video? {
        return try {
            val videoEntity = videoDao.getVideoById(id) ?: return null
            val comments = commentRepository.getCommentsForVideo(id)
            videoEntity.toDomain().copy(comments = comments)
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

            // Save comments to DB via CommentRepository
            remoteDtos.forEach { videoDto ->
                val comments = videoDto.comments ?: emptyList()
                if (comments.isNotEmpty()) {
                    commentRepository.saveComments(videoDto.id, comments)
                }
            }

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
            commentRepository.deleteAllComments()
            fetchRemoteVideos(count)
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error refreshing videos", e)
            getAllVideos()
        }
    }

    /**
     * Toggle like status for a video
     */
    override suspend fun toggleLike(videoId: String): Video? {
        return try {
            val video = videoDao.getVideoById(videoId) ?: return null
            val newIsLiked = !video.isLiked
            val newLikes = if (newIsLiked) video.likes + 1 else video.likes - 1

            videoDao.updateLikeStatus(videoId, newIsLiked, newLikes)
            Log.d("VideoRepositoryImpl", "Toggled like for video $videoId: isLiked=$newIsLiked, likes=$newLikes")

            videoDao.getVideoById(videoId)?.toDomain()
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error toggling like", e)
            null
        }
    }
}

