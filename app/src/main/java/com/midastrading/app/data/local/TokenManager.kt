package com.midastrading.app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure token storage using EncryptedSharedPreferences backed by Android Keystore.
 * All sensitive data (JWT token, user info) is encrypted at rest.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "TokenManager"
        private const val PREFS_NAME = "midas_secure_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create EncryptedSharedPreferences, falling back", e)
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    // Reactive state flows for observing changes
    private val _token = MutableStateFlow<String?>(null)
    private val _userEmail = MutableStateFlow<String?>(null)
    private val _userName = MutableStateFlow<String?>(null)

    val token: Flow<String?> = _token.asStateFlow()
    val userEmail: Flow<String?> = _userEmail.asStateFlow()
    val userName: Flow<String?> = _userName.asStateFlow()
    val isLoggedIn: Flow<Boolean> = _token.map { !it.isNullOrBlank() }

    // Cached token for synchronous access (used by interceptors)
    @Volatile
    var cachedToken: String? = null
        private set

    @Volatile
    var cachedRefreshToken: String? = null
        private set

    init {
        // Load from encrypted storage on init
        cachedToken = encryptedPrefs.getString(KEY_TOKEN, null)
        cachedRefreshToken = encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
        _token.value = cachedToken
        _userEmail.value = encryptedPrefs.getString(KEY_USER_EMAIL, null)
        _userName.value = encryptedPrefs.getString(KEY_USER_NAME, null)
    }

    suspend fun saveToken(token: String) {
        encryptedPrefs.edit().putString(KEY_TOKEN, token).apply()
        cachedToken = token
        _token.value = token
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        encryptedPrefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        cachedRefreshToken = refreshToken
    }

    suspend fun saveUser(email: String, name: String) {
        encryptedPrefs.edit()
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .apply()
        _userEmail.value = email
        _userName.value = name
    }

    suspend fun clearAll() {
        encryptedPrefs.edit().clear().apply()
        cachedToken = null
        cachedRefreshToken = null
        _token.value = null
        _userEmail.value = null
        _userName.value = null
    }
}
