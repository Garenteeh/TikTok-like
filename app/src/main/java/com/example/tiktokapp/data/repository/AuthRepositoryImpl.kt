package com.example.tiktokapp.data.repository

import com.example.tiktokapp.data.datasource.TokenLocalDataSource
import com.example.tiktokapp.data.db.dao.UserDao
import com.example.tiktokapp.data.exceptions.EmailAlreadyTakenException
import com.example.tiktokapp.data.exceptions.UsernameAlreadyTakenException
import com.example.tiktokapp.data.mappers.toDomain
import com.example.tiktokapp.data.mappers.toEntity
import com.example.tiktokapp.data.utils.UserUtils
import com.example.tiktokapp.domain.models.AuthToken
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.repository.AuthRepository
import java.util.Locale
import kotlin.random.Random

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val tokenLocalDataSource: TokenLocalDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val entity = userDao.getUserByEmail(email) ?: return Result.failure(Exception("Utilisateur introuvable"))
            val hashedProvided = UserUtils.hashPasswordWithSalt(password, entity.salt)
            if (entity.password != hashedProvided) {
                return Result.failure(Exception("Identifiants invalides"))
            }

            val token = AuthToken(
                accessToken = "fake_access_${entity.username}_${System.currentTimeMillis()}",
                refreshToken = "fake_refresh_${entity.username}_${System.currentTimeMillis()}",
                expiresIn = 3600L
            )
            tokenLocalDataSource.saveToken(token)

            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(user: User): Result<User> {
        return try {
            if (userDao.isEmailTaken(user.email)) {
                return Result.failure(EmailAlreadyTakenException())
            }

            val firstName = user.firstName
            val lastName = user.lastName

            var finalUsername = user.username.trim()
            if (finalUsername.isBlank()) {
                var baseUsername = if (firstName.isNotBlank()) {
                    (firstName + lastName).lowercase(Locale.getDefault()).replace("\\s+".toRegex(), "")
                } else {
                    user.email.substringBefore("@").lowercase(Locale.getDefault())
                }
                if (baseUsername.isBlank()) baseUsername = "user"
                finalUsername = baseUsername
                var attempt = 0
                while (userDao.isUsernameTaken(finalUsername)) {
                    attempt++
                    finalUsername = "$baseUsername${Random.nextInt(100, 999)}"
                    if (attempt > 10) break
                }
            }

            if (userDao.isUsernameTaken(finalUsername)) {
                return Result.failure(UsernameAlreadyTakenException())
            }

            val salt = UserUtils.generateSalt()
            val hashedPassword = UserUtils.hashPasswordWithSalt(user.password, salt)

            val userToSave = User(
                firstName = firstName,
                lastName = lastName,
                birthDate = user.birthDate,
                email = user.email,
                password = hashedPassword,
                phoneNumber = user.phoneNumber,
                country = user.country,
                username = finalUsername,
                salt = salt
            )

            val entity = userToSave.toEntity()
            userDao.insertUser(entity)

            val saved = userDao.getUserByEmail(user.email) ?: return Result.failure(Exception("Erreur lors de la création de l'utilisateur"))

            val token = AuthToken(
                accessToken = "fake_access_${saved.username}_${System.currentTimeMillis()}",
                refreshToken = "fake_refresh_${saved.username}_${System.currentTimeMillis()}",
                expiresIn = 3600L
            )
            tokenLocalDataSource.saveToken(token)

            Result.success(saved.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(): Result<AuthToken> {
        return try {
            val token = tokenLocalDataSource.getToken()
            if (token == null) return Result.failure(Exception("No token"))
            val newToken = AuthToken(
                accessToken = "refreshed_${token.accessToken}",
                refreshToken = token.refreshToken,
                expiresIn = 3600L
            )
            tokenLocalDataSource.saveToken(newToken)
            Result.success(newToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            tokenLocalDataSource.clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        val token = tokenLocalDataSource.getToken()
        return token != null && token.expiresIn > 0
    }
}
