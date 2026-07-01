package com.example.settings

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun consumeHistory(): Flow<List<String>>
    suspend fun addQuery(query: String)
    suspend fun removeQuery(query: String)
    suspend fun clearHistory()
}
