package com.example.storyapp.data.model.auth

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)