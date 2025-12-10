@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.example.ocrllmfp.data.model
@kotlinx.serialization.Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val theme: String = "DARK",
    val created_at: String? = null
)
