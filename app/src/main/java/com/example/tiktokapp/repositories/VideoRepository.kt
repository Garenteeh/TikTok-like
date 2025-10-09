package com.example.tiktokapp.repositories

import com.example.tiktokapp.models.Comment
import com.example.tiktokapp.models.Video

class VideoRepository {
    fun getVideos(): List<Video> = listOf(
        Video(
            id = "1",
            url = "",
            title = "Big Buck Bunny",
            user = "Alice",
            likes = 123,
            shares = 10,
            reposts = 5,
            comments = listOf(
                Comment(
                    id = "c1",
                    user = "Bob",
                    text = "Super vidéo !",
                    replies = listOf(
                        Comment(
                            id = "c1r1",
                            user = "Alice",
                            text = "Merci !"
                        )
                    )
                ),
                Comment(
                    id = "c2",
                    user = "Charlie",
                    text = "Trop drôle !"
                )
            )
        ),
        Video(
            id = "2",
            url = "",
            title = "Elephant's Dream",
            user = "Bob",
            likes = 87,
            shares = 7,
            reposts = 2,
            comments = listOf()
        )
    )
}

