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
    private val json: Json = Json // You can inject this or create a default instance
) : SessionManager {

    private val sessionKey = stringPreferencesKey("supabase_session")

    override suspend fun deleteSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(sessionKey)
        }
    }

    override suspend fun loadSession(): UserSession? {
        val jsonString = context.dataStore.data.map { preferences ->
            preferences[sessionKey]
        }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    override suspend fun saveSession(session: UserSession) {
        val jsonString = json.encodeToString(session)
        context.dataStore.edit { preferences ->
            preferences[sessionKey] = jsonString
        }
    }
}
