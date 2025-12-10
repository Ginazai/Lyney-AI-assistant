package com.example.ocrllmfp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
// Not yet implemented. For future modularization
class AuthViewModel : ViewModel() {
    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn

    suspend fun signIn(email: String, password: String) {
        _signedIn.value = true
    }

    suspend fun signOut() {
        _signedIn.value = false
    }
}
