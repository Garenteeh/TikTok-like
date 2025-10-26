package com.example.tiktokapp.repositories

import com.example.tiktokapp.models.Comment
import com.example.tiktokapp.models.Video

class VideoRepository {
    fun getVideos(): List<Video> = listOf(
        Video(
            id = "1",
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            title = "Big Buck Bunny 🐰",
            user = "Alice",
            likes = 123,
            shares = 10,
            reposts = 5,
            comments = listOf(
                Comment("c1", "Bob", "Super vidéo !", replies = listOf(
                    Comment("c1r1", "Alice", "Merci !")
                )),
                Comment("c2", "Charlie", "Trop drôle !")
            )
        ),
        Video(
            id = "2",
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            title = "Elephant's Dream 🐘",
            user = "Bob",
            likes = 87,
            shares = 7,
            reposts = 2
        )
    )
}
