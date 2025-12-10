package com.example.ocrllmfp.data.repository

import com.example.ocrllmfp.data.remote.SupabaseClient
import com.example.ocrllmfp.data.remote.UserProfile
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.gotrue.providers.builtin.Email

class AuthRepository {

    private val auth = SupabaseClient.auth
    private val database = SupabaseClient.database

    suspend fun isUserLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        try {
            auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun refreshSession(): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            // Supabase automatically refreshes the session if valid
            val user = auth.currentUserOrNull()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No hay sesión activa"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull() ?: throw Exception("Usuario nulo")

            createUserProfile(user.id, email, name)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(
        email: String,
        password: String
    ): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = auth.currentUserOrNull() ?: throw Exception("Usuario nulo")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): UserInfo? = withContext(Dispatchers.IO) {
        try {
            auth.currentUserOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            // Obtener el usuario actual desde supabase auth
            val user = auth.currentSessionOrNull()?.user
                ?: return@withContext Result.failure(Exception("Usuario no autenticado"))

            // Consulta a la tabla
            val profile = database.from("user_profiles")
                .select {
                    filter {
                        eq("id", user.id)
                    }
                }
                .decodeSingle<UserProfile>()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateUserTheme(userId: String, theme: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            database.from("user_profiles")
                .update({
                    set("theme", theme)
                }) {
                    filter {
                        eq("id", userId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(throw Exception("Ha ocurrido un error al actualizar el tema: ${e}"))
        }
    }

    private suspend fun createUserProfile(userId: String, email: String, name: String) {
        try {
            val profile = UserProfile(
                id = userId,
                email = email,
                name = name,
                theme = "DARK"
            )
            database.from("user_profiles").insert(profile)
        } catch (e: Exception) {
            // Log error pero no falla el signup
        }
    }
}