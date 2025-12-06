package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.UserEntity
import com.example.tiktokapp.domain.models.User


/**
 * Mapper functions to convert between User data models
 */
fun UserEntity.toDomain(): User {
    return User(
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        password = this.password,
        phoneNumber = this.phoneNumber,
        birthDate = this.birthDate,
        country = this.country,
        salt = this.salt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        password = this.password,
        phoneNumber = this.phoneNumber,
        birthDate = this.birthDate,
        country = this.country,
        salt = this.salt
    )
}