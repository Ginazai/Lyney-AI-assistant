package com.example.ocrllmfp.data.remote

import com.example.ocrllmfp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 15000
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    suspend fun analyzeText(
        extractedText: String,
        context: String = "general"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(extractedText, context)

            val response = model.generateContent(prompt)
            val aiResponse = response.text ?: throw Exception("Respuesta vacía de Gemini")

            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPrompt(text: String, context: String): String {
        val systemInstruction = when (context) {
            "code" -> """
                Eres un asistente experto en programación. Analiza el siguiente código y:
                1. Identifica el lenguaje de programación
                2. Explica qué hace el código línea por línea
                3. Sugiere mejoras si es necesario
                4. Detecta posibles errores o bugs
                
                Responde en formato Markdown con bloques de código cuando sea necesario.
                
                Nota: el origen de código es OCR por lo que se espera esté desordenado.
            """.trimIndent()

            "math" -> """
                Eres un profesor de matemáticas experto. Analiza el siguiente problema matemático y:
                1. Identifica el tipo de problema
                2. Proporciona una solución paso a paso
                3. Explica los conceptos matemáticos involucrados
                4. Usa notación matemática clara
                
                Responde en formato Markdown.
                
                Nota: el origen de código es OCR por lo que se espera esté desordenado.
            """.trimIndent()

            else -> """
                Eres un asistente inteligente que analiza texto extraído de imágenes.
                Tu trabajo es:
                1. Identificar el tipo de contenido (texto, código, matemáticas, tabla, etc.)
                2. Proporcionar una explicación clara y útil
                3. Organizar la información de manera legible
                4. Sugerir acciones útiles basadas en el contenido
                5. En caso de no poder identificar la naturaleza del contenido, informa de esto al usuario y solicita que intente nuevamente.
                
                Responde siempre en español y en formato Markdown para mejor legibilidad.
                
                Nota: el origen de código es OCR por lo que se espera esté desordenado.
            """.trimIndent()
        }

        return """
            $systemInstruction
            
            Texto extraído:
```
            $text
```
            
            Por favor, analiza y responde brevemente en español.
        """.trimIndent()
    }

    fun detectContext(text: String): String {
        return when {
            isCode(text) -> "code"
            isMath(text) -> "math"
            else -> "general"
        }
    }

    private fun isCode(text: String): Boolean {
        val codeIndicators = listOf(
            "function", "def ", "class ", "import ", "const ", "let ", "var ",
            "public ", "private ", "void ", "return ", "{", "}", ";", "//", "/*"
        )
        val lowerText = text.lowercase()
        return codeIndicators.count { lowerText.contains(it) } >= 3
    }

    private fun isMath(text: String): Boolean {
        val mathIndicators = listOf(
            "=", "+", "-", "×", "÷", "∫", "∑", "√", "²", "³",
            "sin", "cos", "tan", "log", "ln"
        )
        return mathIndicators.count { text.contains(it) } >= 2
    }
}