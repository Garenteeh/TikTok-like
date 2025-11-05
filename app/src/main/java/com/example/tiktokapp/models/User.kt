package com.example.tiktokapp.models

import java.util.Date

data class User(
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val country: String,
    val username: String,

)
