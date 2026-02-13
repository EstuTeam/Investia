package com.investia.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.investia.app.domain.model.*

// ===== Auth =====
data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String
)

data class RefreshTokenRequestDto(
    @SerializedName("refresh_token") val refreshToken: String
)

data class ChangePasswordDto(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

// Backend wraps auth data in nested "data" object:
// { success, message, data: { access_token, refresh_token, user: {id, email, full_name} } }
// Error responses: { detail: "Invalid email or password" }
data class AuthResponseDto(
    val success: Boolean = false,
    val token: String = "",
    @SerializedName("access_token") val accessToken: String = "",
    @SerializedName("refresh_token") val refreshToken: String = "",
    val user: UserDto? = null,
    val data: AuthDataDto? = null,
    val message: String = "",
    val detail: String = ""
) {
    fun toDomain() = AuthResponse(
        success = success,
        token = token.ifBlank { data?.accessToken ?: accessToken },
        refreshToken = data?.refreshToken ?: refreshToken,
        user = (data?.user ?: user)?.toDomain(),
        message = message.ifBlank { detail }
    )
}

data class AuthDataDto(
    @SerializedName("access_token") val accessToken: String = "",
    @SerializedName("refresh_token") val refreshToken: String = "",
    val user: UserDto? = null
)

data class UserDto(
    val id: String = "",
    val email: String = "",
    @SerializedName("full_name") val fullNameSnake: String = "",
    val fullName: String = "",
    @SerializedName("is_active") val isActiveSnake: Boolean = true,
    val isActive: Boolean = true,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    val membership: String = "free"
) {
    fun toDomain() = AuthUser(
        id = id,
        email = email,
        fullName = fullName.ifBlank { fullNameSnake },
        isActive = isActive && isActiveSnake
    )
}
