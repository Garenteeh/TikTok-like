package com.example.tiktokapp.data.repository

import android.util.Log
import com.example.tiktokapp.data.db.dao.UserDao
import com.example.tiktokapp.data.mappers.toDomain
import com.example.tiktokapp.data.mappers.toEntity
import com.example.tiktokapp.data.utils.UserUtils
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.UserRepository
import com.example.tiktokapp.data.exceptions.EmailAlreadyTakenException
import com.example.tiktokapp.data.exceptions.UsernameAlreadyTakenException

class UserRepositoryImpl(
    private val userDao: UserDao,
) : UserRepository {

    override suspend fun createUser(user: User) {

        if(userDao.isEmailTaken(user.email)) {
            Log.e("UserRepositoryImpl", "Email already taken")
            throw EmailAlreadyTakenException()
        }
        if(userDao.isUsernameTaken(user.username)) {
            Log.e("UserRepositoryImpl", "Username already taken")
            throw UsernameAlreadyTakenException()
        }
        // Générer un sel et hasher le mot de passe avec PBKDF2
        val salt = UserUtils.generateSalt()
        val hashedPassword = UserUtils.hashPasswordWithSalt(user.password, salt)
        val userToSave = user.copy(password = hashedPassword, salt = salt)
        val userEntity = userToSave.toEntity()
        userDao.insertUser(userEntity)
    }

    override suspend fun getUserByEmail(email: String): User? {
        val entity = userDao.getUserByEmail(email)
        return entity?.toDomain()
    }

    override suspend fun getUserByUsername(username: String): User? {
        val entity = userDao.getUserByUsername(username)
        return entity?.toDomain()
    }

    override suspend fun login(
        identifier: String,
        password: String
    ): User? {
        // Récupérer l'utilisateur, hasher le mot de passe fourni avec le sel stocké, comparer
        val entity = if (identifier.contains("@")) {
            userDao.getUserByEmail(identifier)
        } else {
            userDao.getUserByUsername(identifier)
        }

        if (entity == null) return null

        val salt = entity.salt
        val hashedProvided = UserUtils.hashPasswordWithSalt(password, salt)
        return if (hashedProvided == entity.password) entity.toDomain() else null
    }
}