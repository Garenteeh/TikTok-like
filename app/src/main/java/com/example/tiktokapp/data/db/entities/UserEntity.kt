package com.example.tiktokapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID autogénéré par Room
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val password: String, // Hashed password
    val salt: String,
    val phoneNumber: String,
    val country: String,
    val username: String

)