package com.example.ocrllmfp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.jan.supabase.gotrue.SessionManager
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "supabase_session")

class AndroidSessionManager(
    private val context: Context,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) : SessionManager {

    private val sessionKey = stringPreferencesKey("supabase_session")

    override suspend fun deleteSession() {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(sessionKey)
            }
        } catch (e: Exception) {
            android.util.Log.e("SessionManager", "Error deleting session: ${e.message}")
            throw e
        }
    }

    override suspend fun loadSession(): UserSession? {
        return try {
            val jsonString = context.dataStore.data.map { preferences ->
                preferences[sessionKey]
            }.first()

            jsonString?.let {
                json.decodeFromString<UserSession>(it)
            }
        } catch (e: Exception) {
            android.util.Log.e("SessionManager", "Error loading session: ${e.message}")
            null
        }
    }

    override suspend fun saveSession(session: UserSession) {
        try {
            val jsonString = json.encodeToString(session)
            context.dataStore.edit { preferences ->
                preferences[sessionKey] = jsonString
            }
        } catch (e: Exception) {
            android.util.Log.e("SessionManager", "Error saving session: ${e.message}")
            throw e
        }
    }
}