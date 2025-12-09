package com.example.ocrllmfp.data.model

data class UserProfile(
    val id: String,
    val email: String,
    val password: String,
    val displayName: String? = null
)
