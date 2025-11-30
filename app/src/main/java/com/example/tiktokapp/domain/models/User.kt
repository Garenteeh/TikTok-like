package com.example.tiktokapp.domain.models

data class User(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val country: String,
    val username: String,
    val salt: String = ""

)