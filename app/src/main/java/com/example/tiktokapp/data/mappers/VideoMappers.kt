package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.VideoEntity
import com.example.tiktokapp.domain.models.Video
import com.example.tiktokapp.domain.models.dto.VideoDto


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

fun VideoEntity.toDomain(): Video {
    return Video(
        id = this.id,
        url = this.url,
        title = this.title,
        user = this.user,
        likes = this.likes,
        shares = this.shares,
        reposts = this.reposts,
        comments = emptyList(),
        isLiked = this.isLiked,
        isReposted = this.isReposted
    )
}

fun Video.toEntity(): VideoEntity {
    return VideoEntity(
        id = this.id,
        url = this.url,
        title = this.title,
        user = this.user,
        likes = this.likes,
        shares = this.shares,
        reposts = this.reposts,
        isLiked = this.isLiked,
        isReposted = this.isReposted
    )
}

