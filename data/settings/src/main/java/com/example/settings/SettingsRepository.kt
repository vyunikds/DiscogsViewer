package com.example.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun consumeThemeMode(): Flow<Int>
    suspend fun setThemeMode(mode: Int)
}
