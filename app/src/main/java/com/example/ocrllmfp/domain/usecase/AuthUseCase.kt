package com.example.ocrllmfp.domain.usecase

import com.example.ocrllmfp.data.repository.AuthRepository

class AuthUseCase(private val repo: AuthRepository = AuthRepository()) {
    suspend fun signIn(email: String, password: String) = repo.signIn(email, password)
    suspend fun signUp(email: String, password: String, name: String) = repo.signUp(email, password, name)
}