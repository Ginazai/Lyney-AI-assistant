package com.example.ocrllmfp.data.repository

import com.example.ocrllmfp.data.model.QueryHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryRepository {
    private val _history = MutableStateFlow<List<QueryHistory>>(emptyList())
    val history: StateFlow<List<QueryHistory>> = _history

    suspend fun add(entry: QueryHistory) {
        _history.value = _history.value + entry
    }
}
