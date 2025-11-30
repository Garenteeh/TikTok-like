package com.example.tiktokapp.data.mappers

import com.example.tiktokapp.data.db.entities.UserEntity
import com.example.tiktokapp.domain.models.User


/**
 * Mapper functions to convert between User data models
 */

// Entity to Domain Model
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

// Domain Model to Entity (for saving to DB)
fun User.toEntity(): UserEntity {
    return UserEntity(
        // id est auto-généré par Room s'il vaut 0, sinon il met à jour l'existant
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