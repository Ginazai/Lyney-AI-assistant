package com.example.ocrllmfp.data.model

data class QueryHistory(
    val id: String,
    val queryText: String,
    val resultText: String?,
    val timestamp: Long
)