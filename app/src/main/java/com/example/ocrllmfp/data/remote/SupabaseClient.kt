@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.ocrllmfp.data.remote

import android.content.Context
import com.example.ocrllmfp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseClientProvider {

    private var _client: SupabaseClient? = null

    fun getClient(context: Context): SupabaseClient {
        if (_client == null) {
            _client = createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_KEY
            ) {
                install(Auth) {
                    // Habilitar auto-refresh de tokens
                    autoLoadFromStorage = true
                    autoSaveToStorage = true
                    alwaysAutoRefresh = true
                }
                install(Postgrest)
            }
        }
        return _client!!
    }

    fun auth(context: Context) = getClient(context).auth
    fun database(context: Context) = getClient(context).postgrest
}

@kotlinx.serialization.Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val theme: String = "DARK",
    val created_at: String? = null
)