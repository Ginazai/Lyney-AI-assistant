package com.example.ocrllmfp.data.remote

import android.content.Context
import com.example.ocrllmfp.BuildConfig
import com.example.ocrllmfp.data.local.datastore.AndroidSessionManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseClientProvider {
    @Volatile
    private var _client: SupabaseClient? = null

    // Importante: sincronizar el acceso
    fun getClient(context: Context): SupabaseClient {
        return _client ?: synchronized(this) {
            _client ?: createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_KEY
            ) {
                install(Auth) {
                    // CRÍTICO: usar applicationContext para evitar memory leaks
                    sessionManager = AndroidSessionManager(context.applicationContext)
                    alwaysAutoRefresh = true
                    autoLoadFromStorage = true
                    autoSaveToStorage = true
                }
                install(Postgrest)
            }.also { _client = it }
        }
    }

    fun auth(context: Context) = getClient(context).auth
    fun database(context: Context) = getClient(context).postgrest
}