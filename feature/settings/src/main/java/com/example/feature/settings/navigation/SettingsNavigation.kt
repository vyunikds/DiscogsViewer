package com.example.feature.settings.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.settings.SettingsViewModel
import com.example.feature.settings.state.SettingsScreenState
import com.example.feature.settings.ui.SettingsScreen

@Composable
fun SettingsScreenRoute(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state: SettingsScreenState by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onBack = onBack,
        onThemeModeChanged = viewModel::setThemeMode,
    )
}
