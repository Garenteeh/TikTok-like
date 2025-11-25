package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.VideoEntity
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.models.dto.VideoDto

/**
 * Mapper functions to convert between different data models
 */

// DTO to Domain Model
fun VideoDto.toDomain(): Video {
    return Video(
        id = this.id,
        url = this.url,
        title = this.title,
        user = this.user,
        likes = this.likes,
        shares = this.shares,
        reposts = this.reposts,
        comments = this.comments
    )
}

// DTO to Entity
fun VideoDto.toEntity(): VideoEntity {
    return VideoEntity(
        id = this.id,
        url = this.url,
        title = this.title,
        user = this.user,
        likes = this.likes,
        shares = this.shares,
        reposts = this.reposts
    )
}

// Entity to Domain Model
fun VideoEntity.toDomain(): Video {
    return Video(
        id = this.id,
        url = this.url,
        title = this.title,
        user = this.user,
        likes = this.likes,
        shares = this.shares,
        reposts = this.reposts,
        comments = emptyList() // Comments are not stored in local DB
    )
}

// Domain Model to Entity (for saving to DB)
fun Video.toEntity(): VideoEntity {
    return VideoEntity(
        id = this.id,
        url = this.url,
        title = this.title,
        user = this.user,
        likes = this.likes,
        shares = this.shares,
        reposts = this.reposts
    )
}

