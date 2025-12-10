package com.example.ocrllmfp.data.local.datastore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
// currently not implemented, for further modularization
class PreferencesManager {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    suspend fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }
}
