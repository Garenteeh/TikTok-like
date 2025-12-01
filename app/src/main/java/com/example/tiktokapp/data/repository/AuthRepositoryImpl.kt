package com.example.tiktokapp.data.repository

import com.example.tiktokapp.data.datasource.TokenLocalDataSource
import com.example.tiktokapp.data.db.dao.UserDao
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
            // Essayer de récupérer l'utilisateur enregistré par email
            val entity = userDao.getUserByEmail(email) ?: return Result.failure(Exception("Utilisateur introuvable"))

            // Hasher le mot de passe fourni avec le sel stocké et comparer
            val hashedProvided = UserUtils.hashPasswordWithSalt(password, entity.salt)
            if (entity.password != hashedProvided) {
                return Result.failure(Exception("Identifiants invalides"))
            }

            // Créer un token factice et le sauvegarder
            val token = AuthToken(
                accessToken = "fake_access_${entity.username}_${System.currentTimeMillis()}",
                refreshToken = "fake_refresh_${entity.username}_${System.currentTimeMillis()}",
                expiresIn = 3600L
            )
            tokenLocalDataSource.saveToken(token)

            // Retourner l'utilisateur en domaine
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            // Vérifier si l'email est déjà pris
            if (userDao.isEmailTaken(email)) {
                return Result.failure(Exception("Email déjà utilisé"))
            }

            // Déterminer prénom / nom à partir du champ name
            val parts = name.trim().split(" ", limit = 2)
            val firstName = parts.getOrNull(0) ?: ""
            val lastName = parts.getOrNull(1) ?: ""

            // Générer un username à partir du nom ou de l'email, et s'assurer qu'il est unique
            var baseUsername = if (firstName.isNotBlank()) {
                (firstName + lastName).lowercase(Locale.getDefault()).replace("\\s+".toRegex(), "")
            } else {
                email.substringBefore("@").lowercase(Locale.getDefault())
            }
            if (baseUsername.isBlank()) baseUsername = "user"

            var username = baseUsername
            var attempt = 0
            while (userDao.isUsernameTaken(username)) {
                attempt++
                username = "$baseUsername${Random.nextInt(100, 999)}"
                if (attempt > 10) break
            }

            // Générer sel et hasher le mot de passe
            val salt = UserUtils.generateSalt()
            val hashedPassword = UserUtils.hashPasswordWithSalt(password, salt)

            // Construire l'entité utilisateur avec valeurs par défaut pour les champs manquants
            val userToSave = User(
                firstName = firstName,
                lastName = lastName,
                birthDate = "",
                email = email,
                password = hashedPassword,
                phoneNumber = "",
                country = "",
                username = username,
                salt = salt
            )

            // Convertir en entity et insérer
            val entity = userToSave.toEntity()
            userDao.insertUser(entity)

            // Récupérer l'utilisateur inséré
            val saved = userDao.getUserByEmail(email) ?: return Result.failure(Exception("Erreur lors de la création de l'utilisateur"))

            // Générer token factice et sauvegarder
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
            // Ici on ne contacte pas de serveur, on simule le refresh
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
