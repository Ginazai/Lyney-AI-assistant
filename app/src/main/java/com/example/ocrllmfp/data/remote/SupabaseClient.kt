@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.ocrllmfp.data.remote

import com.example.ocrllmfp.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseClient {

    private const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private const val SUPABASE_KEY = BuildConfig.SUPABASE_KEY

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }

    val auth = client.auth
    val database = client.postgrest
}

@kotlinx.serialization.Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val theme: String = "DARK",
    val created_at: String? = null
)