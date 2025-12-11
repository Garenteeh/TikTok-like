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


    override suspend fun fetchRemoteVideos(count: Int): List<Video> {
        return try {
            Log.d("VideoRepositoryImpl", "Fetching videos from API...")

            val remoteDtos = remoteDataSource.fetchVideos(count)

            val entities = remoteDtos.map { it.toEntity() }
            videoDao.insertAll(entities)
            Log.d("VideoRepositoryImpl", "Saved ${entities.size} videos to DB")

            remoteDtos.forEach { videoDto ->
                val comments = videoDto.comments ?: emptyList()
                if (comments.isNotEmpty()) {
                    commentRepository.saveComments(videoDto.id, comments)
                }
            }

            remoteDtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error fetching remote videos", e)
            getAllVideos()
        }
    }


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

    override suspend fun createVideo(video: Video): Video? {
        return try {
            val videoEntity = video.toEntity()
            videoDao.insertVideo(videoEntity)
            videoDao.getVideoById(video.id)?.toDomain()
        } catch (e: Exception) {
            Log.e("VideoRepositoryImpl", "Error creating video", e)
            null
        }
    }
}
