package com.example.discogsviewer.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SettingsScreenPO(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SettingsScreenPO>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("settings_screen") },
    ) {
    val backButton: KNode = child {
        useUnmergedTree = true
        hasTestTag("settings_back_button")
    }

    val themeSystem: KNode = child {
        hasTestTag("theme_system")
    }

    val themeLight: KNode = child {
        hasTestTag("theme_light")
    }

    val themeDark: KNode = child {
        hasTestTag("theme_dark")
    }
}
