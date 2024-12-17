package com.example.storyapp.data.model.auth

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)