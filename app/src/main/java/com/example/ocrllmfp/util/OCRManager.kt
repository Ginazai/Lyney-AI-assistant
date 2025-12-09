package com.example.ocrllmfp.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OCRManager(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextFromBitmap(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val text = processImage(image)
            val cleanedText = preprocessText(text)

            if (cleanedText.isBlank()) {
                Result.failure(Exception("No se detectó texto en la imagen"))
            } else {
                Result.success(cleanedText)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun processImage(image: InputImage): String =
        suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

    private fun preprocessText(text: String): String {
        if (text.isBlank()) return text

        return text
            // Normalizar espacios en blanco
            .replace(Regex("\\s+"), " ")
            // Eliminar espacios al inicio y final
            .trim()
            // Normalizar saltos de línea múltiples
            .replace(Regex("\n{3,}"), "\n\n")
            // Eliminar caracteres extraños comunes en OCR
            .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "")
    }

    fun analyzeTextStructure(text: String): TextStructure {
        val hasCode = detectCode(text)
        val hasMath = detectMath(text)
        val lineCount = text.lines().size

        return TextStructure(
            hasCode = hasCode,
            hasMath = hasMath,
            lineCount = lineCount,
            characterCount = text.length,
            estimatedType = when {
                hasCode -> TextType.CODE
                hasMath -> TextType.MATH
                lineCount < 5 -> TextType.SHORT_TEXT
                else -> TextType.LONG_TEXT
            }
        )
    }

    private fun detectCode(text: String): Boolean {
        val codePatterns = listOf(
            Regex("function\\s+\\w+\\s*\\("),
            Regex("def\\s+\\w+\\s*\\("),
            Regex("class\\s+\\w+"),
            Regex("(public|private|protected)\\s+"),
            Regex("import\\s+[\\w.]+"),
            Regex("const\\s+\\w+\\s*="),
            Regex("\\{[\\s\\S]*\\}"),
            Regex("//.*|/\\*[\\s\\S]*?\\*/")
        )
        return codePatterns.count { it.containsMatchIn(text) } >= 2
    }

    private fun detectMath(text: String): Boolean {
        val mathSymbols = listOf("∫", "∑", "∏", "√", "≈", "≠", "≤", "≥", "∞", "π", "θ")
        val mathOperators = listOf("sin", "cos", "tan", "log", "ln", "lim")
        val equations = Regex("\\w+\\s*=\\s*[\\d\\w+\\-*/^()]+")

        val hasSymbols = mathSymbols.any { text.contains(it) }
        val hasOperators = mathOperators.any { text.contains(it, ignoreCase = true) }
        val hasEquations = equations.containsMatchIn(text)

        return hasSymbols || hasOperators || hasEquations
    }

    fun release() {
        recognizer.close()
    }
}

data class TextStructure(
    val hasCode: Boolean,
    val hasMath: Boolean,
    val lineCount: Int,
    val characterCount: Int,
    val estimatedType: TextType
)

enum class TextType {
    CODE, MATH, SHORT_TEXT, LONG_TEXT
}
