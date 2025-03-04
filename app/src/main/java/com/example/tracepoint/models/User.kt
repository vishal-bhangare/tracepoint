package com.example.tracepoint.models

data class User(
    val _id: String,
    val name: String,
    val email: String,
    val contact: String?,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Boolean = false,
    val _id: String? = null
)


data class RegisterRequest(
    val name: String,
    val email: String,
    val contact: String?,
    val password: String,
    val confirmPassword: String
)

data class AuthResponse(
    val token: String,
    val user: User
)
