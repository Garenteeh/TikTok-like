package com.example.tiktokapp.data.datasource

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.tiktokapp.domain.models.AuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * TokenLocalDataSource: stocke les tokens dans EncryptedSharedPreferences pour sécurité au repos.
 */
class TokenLocalDataSource(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    suspend fun saveToken(token: AuthToken) {
        withContext(Dispatchers.IO) {
            encryptedPrefs.edit()
                .putString("access_token", token.accessToken)
                .putString("refresh_token", token.refreshToken)
                .putLong("expires_at", System.currentTimeMillis() + (token.expiresIn * 1000))
                .apply()
        }
    }

    suspend fun getToken(): AuthToken? {
        return withContext(Dispatchers.IO) {
            val accessToken = encryptedPrefs.getString("access_token", null)
            val refreshToken = encryptedPrefs.getString("refresh_token", null)
            val expiresAt = encryptedPrefs.getLong("expires_at", 0L)

            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank() || expiresAt == 0L) {
                null
            } else {
                AuthToken(accessToken, refreshToken, (expiresAt - System.currentTimeMillis()) / 1000)
            }
        }
    }

    suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            encryptedPrefs.edit().clear().apply()
        }
    }
}
