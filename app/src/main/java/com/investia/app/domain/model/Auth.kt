package com.investia.app.domain.model

data class AuthUser(
    val id: String = "",
    val email: String,
    val fullName: String,
    val token: String = "",
    val isActive: Boolean = true
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

data class AuthResponse(
    val success: Boolean,
    val token: String = "",
    val refreshToken: String = "",
    val user: AuthUser? = null,
    val message: String = ""
)
