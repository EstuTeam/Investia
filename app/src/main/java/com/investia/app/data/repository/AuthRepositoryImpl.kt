package com.investia.app.data.repository

import com.investia.app.data.local.TokenManager
import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.data.remote.dto.LoginRequestDto
import com.investia.app.data.remote.dto.RegisterRequestDto
import com.investia.app.domain.model.AuthResponse
import com.investia.app.domain.repository.AuthRepository
import com.investia.app.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: InvestiaApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenManager.isLoggedIn
    override val currentUserEmail: Flow<String?> = tokenManager.userEmail
    override val currentUserName: Flow<String?> = tokenManager.userName

    override suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.login(LoginRequestDto(email, password))
            val authResponse = response.toDomain()
            if (authResponse.success && authResponse.token.isNotBlank()) {
                tokenManager.saveToken(authResponse.token)
                if (authResponse.refreshToken.isNotBlank()) {
                    tokenManager.saveRefreshToken(authResponse.refreshToken)
                }
                authResponse.user?.let { user ->
                    tokenManager.saveUser(user.email, user.fullName)
                }
            }
            Resource.Success(authResponse)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Giriş başarısız")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): Resource<AuthResponse> {
        return try {
            val response = api.register(RegisterRequestDto(email, password, fullName))
            val authResponse = response.toDomain()
            if (authResponse.success && authResponse.token.isNotBlank()) {
                tokenManager.saveToken(authResponse.token)
                if (authResponse.refreshToken.isNotBlank()) {
                    tokenManager.saveRefreshToken(authResponse.refreshToken)
                }
                authResponse.user?.let { user ->
                    tokenManager.saveUser(user.email, user.fullName)
                }
            }
            Resource.Success(authResponse)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Kayıt başarısız")
        }
    }

    override suspend fun logout() {
        // Try to call server logout (invalidates token server-side)
        try {
            api.logout()
        } catch (_: Exception) {
            // Server logout failed — still clear local tokens
        }
        tokenManager.clearAll()
    }
}
