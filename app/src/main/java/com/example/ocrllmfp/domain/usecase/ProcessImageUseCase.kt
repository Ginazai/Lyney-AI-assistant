package com.example.ocrllmfp.domain.usecase

import com.example.ocrllmfp.util.OCRManager

//class ProcessImageUseCase(
//    private val ocrManager: OCRManager = OCRManager,
//    private val aiService: ClaudeService = ClaudeService()
//) {
//    suspend operator fun invoke(imagePath: String): Result<Pair<String?, String>> {
//        val text = ocrManager.extractTextFromImage(imagePath)
//        val ai = aiService.analyzeText(text ?: "")
//        return ai.map { analysis -> Pair(text, analysis) }
//    }
//}
