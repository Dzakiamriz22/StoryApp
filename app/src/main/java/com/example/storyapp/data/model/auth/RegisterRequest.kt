package com.example.storyapp.data.model.auth

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)