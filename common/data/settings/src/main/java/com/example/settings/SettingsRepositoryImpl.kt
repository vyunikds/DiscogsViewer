package com.example.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val themeModeKey = intPreferencesKey("theme_mode")

    override fun consumeThemeMode(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[themeModeKey] ?: 0
        }
    }

    override suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[themeModeKey] = mode
        }
    }
}
