package com.jgg0328.demo.dto

data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: String,
    val email: String,
    val name: String,
)

data class ProfileResponse(
    val id: String,
    val email: String,
    val name: String
)