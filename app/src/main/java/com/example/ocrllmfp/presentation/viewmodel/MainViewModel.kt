package com.example.ocrllmfp.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocrllmfp.util.OCRManager
import com.example.ocrllmfp.data.remote.GeminiService
import com.example.ocrllmfp.util.TextStructure
import com.example.ocrllmfp.data.repository.AuthRepository
import com.example.ocrllmfp.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    private val authRepository: AuthRepository = AuthRepository(context),
    private val ocrManager: OCRManager = OCRManager(context),
    private val geminiService: GeminiService = GeminiService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingSession = true) }

            // Dar tiempo a Supabase para cargar la sesión del storage
            kotlinx.coroutines.delay(500)

            val result = authRepository.refreshSession()

            result.onSuccess { user ->
                android.util.Log.d("MainViewModel", "Sesión restaurada: ${user.id}")
                _uiState.update {
                    it.copy(
                        isCheckingSession = false,
                        isAuthenticated = true,
                        currentUser = user
                    )
                }
                loadUser()
                loadUserTheme(user.id)
            }.onFailure { error ->
                android.util.Log.d("MainViewModel", "No hay sesión: ${error.message}")
                _uiState.update {
                    it.copy(
                        isCheckingSession = false,
                        isAuthenticated = false
                    )
                }
            }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val result = authRepository.getUserProfile()

            result.onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = profile.name
                    )
                }
                loadUserTheme(profile.id)
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = "Usuario"
                    )
                }
            }
        }
    }

    private fun loadUserTheme(userId: String) {
        viewModelScope.launch {
            authRepository.getUserProfile()
                .onSuccess { profile ->
                    try {
                        val theme = AppTheme.valueOf(profile.theme)
                        _uiState.update { it.copy(currentTheme = theme) }
                        android.util.Log.d("MainViewModel", "Tema cargado: $theme")
                    } catch (e: Exception) {
                        android.util.Log.e("MainViewModel", "Tema inválido: ${profile.theme}")
                    }
                }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authRepository.signIn(email, password)
                .onSuccess { user ->
                    android.util.Log.d("MainViewModel", "Login exitoso: ${user.id}")
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            isLoading = false
                        )
                    }
                    loadUser()
                    loadUserTheme(user.id)
                }
                .onFailure { error ->
                    android.util.Log.e("MainViewModel", "Error login: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al iniciar sesión"
                        )
                    }
                }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authRepository.signUp(email, password, name)
                .onSuccess { user ->
                    android.util.Log.d("MainViewModel", "Registro exitoso: ${user.id}")
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            isLoading = false,
                            userName = name
                        )
                    }
                }
                .onFailure { error ->
                    android.util.Log.e("MainViewModel", "Error registro: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al registrarse"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            android.util.Log.d("MainViewModel", "Sesión cerrada")
            _uiState.update { MainUiState() }
        }
    }

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch {
            // Actualizar UI inmediatamente
            _uiState.update { it.copy(currentTheme = theme) }

            // Guardar en Supabase en background
            val user = authRepository.getCurrentUser()
            user?.let {
                authRepository.updateUserTheme(it.id, theme.name)
                    .onFailure { error ->
                        android.util.Log.e("MainViewModel", "Error guardando tema: ${error.message}")
                        // Opcional: mostrar toast o mensaje al usuario
                    }
            }
        }
    }

    fun toggleTheme() {
        val nextTheme = when (_uiState.value.currentTheme) {
            AppTheme.LIGHT -> AppTheme.DARK
            AppTheme.DARK -> AppTheme.BLUE
            AppTheme.BLUE -> AppTheme.GREEN
            AppTheme.GREEN -> AppTheme.PURPLE
            AppTheme.PURPLE -> AppTheme.LIGHT
        }
        changeTheme(nextTheme) // ← Usa changeTheme que guarda en Supabase
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            // Paso 1: Iniciar procesamiento
            _uiState.update {
                it.copy(
                    isProcessing = true,
                    processingStage = ProcessingStage.OCR,
                    error = null,
                    capturedImage = bitmap
                )
            }

            // Paso 2: Extraer texto con OCR
            ocrManager.extractTextFromBitmap(bitmap)
                .onSuccess { extractedText ->
                    _uiState.update {
                        it.copy(
                            extractedText = extractedText,
                            processingStage = ProcessingStage.ANALYZING
                        )
                    }

                    // Analizar estructura del texto
                    val structure = ocrManager.analyzeTextStructure(extractedText)
                    _uiState.update { it.copy(textStructure = structure) }

                    // Paso 3: Enviar a Gemini
                    _uiState.update {
                        it.copy(processingStage = ProcessingStage.AI_PROCESSING)
                    }

                    val context = geminiService.detectContext(extractedText)

                    geminiService.analyzeText(extractedText, context)
                        .onSuccess { aiResponse ->
                            _uiState.update {
                                it.copy(
                                    isProcessing = false,
                                    processingStage = ProcessingStage.COMPLETE,
                                    aiResponse = aiResponse
                                )
                            }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isProcessing = false,
                                    processingStage = ProcessingStage.IDLE,
                                    error = "Error en IA: ${error.message}"
                                )
                            }
                        }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            processingStage = ProcessingStage.IDLE,
                            error = "Error al extraer texto: ${error.message}"
                        )
                    }
                }
        }
    }

    private fun generateMockAIResponse(text: String, structure: TextStructure): String {
        return buildString {
            appendLine("# Análisis del Texto\n")
            appendLine("**Tipo detectado:** ${structure.estimatedType}\n")
            appendLine("**Características:**")
            appendLine("- ${structure.lineCount} líneas")
            appendLine("- ${structure.characterCount} caracteres")
            if (structure.hasCode) appendLine("- Contiene código")
            if (structure.hasMath) appendLine("- Contiene matemáticas")
            appendLine("\n## Contenido:")
            appendLine("```")
            appendLine(text.take(500)) // Primeros 500 caracteres
            if (text.length > 500) appendLine("...")
            appendLine("```")
        }
    }

    fun resetProcessing() {
        _uiState.update {
            it.copy(
                extractedText = null,
                aiResponse = null,
                capturedImage = null,
                textStructure = null,
                processingStage = ProcessingStage.IDLE,
                error = null,
                isProcessing = false
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        ocrManager.release()
    }
}

data class MainUiState(
    val isAuthenticated: Boolean = false,
    val currentUser: Any? = null,
    val currentTheme: AppTheme = AppTheme.DARK,
    val isLoading: Boolean = false,
    val isCheckingSession: Boolean = false,
    val isProcessing: Boolean = false,
    val processingStage: ProcessingStage = ProcessingStage.IDLE,
    val capturedImage: Bitmap? = null,
    val extractedText: String? = null,
    val textStructure: TextStructure? = null,
    val aiResponse: String? = null,
    val error: String? = null,
    val userName: String? = null
)