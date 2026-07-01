package com.example.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val SEARCH_HISTORY_MAX = 50
private const val SEPARATOR = "\u001F"
private val historyKey = stringPreferencesKey("search_history")

private val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SearchHistoryRepository {

    private fun parseHistory(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(SEPARATOR).filter { it.isNotBlank() }
    }

    private fun serializeHistory(list: List<String>): String = list.joinToString(SEPARATOR)

    override fun consumeHistory(): Flow<List<String>> {
        return context.searchHistoryDataStore.data.map { preferences ->
            parseHistory(preferences[historyKey])
        }
    }

    override suspend fun addQuery(query: String) {
        val sanitized = query.trim()
        if (sanitized.isEmpty()) return

        context.searchHistoryDataStore.edit { preferences ->
            val current = parseHistory(preferences[historyKey])
            val withoutQuery = current.filter { it != sanitized }
            val updated = withoutQuery.toMutableList().apply { add(sanitized) }
            if (updated.size > SEARCH_HISTORY_MAX) {
                updated.removeAt(0)
            }
            preferences[historyKey] = serializeHistory(updated)
        }
    }

    override suspend fun removeQuery(query: String) {
        context.searchHistoryDataStore.edit { preferences ->
            val current = parseHistory(preferences[historyKey])
            val updated = current.filter { it != query }
            preferences[historyKey] = serializeHistory(updated)
        }
    }

    override suspend fun clearHistory() {
        context.searchHistoryDataStore.edit { preferences ->
            preferences.remove(historyKey)
        }
    }
}
