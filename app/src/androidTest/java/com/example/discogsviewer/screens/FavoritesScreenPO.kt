package com.example.discogsviewer.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class FavoritesScreenPO(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FavoritesScreenPO>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("favorites_screen") },
    ) {
    val loadingIndicator: KNode = child {
        hasTestTag("favorites_loading")
    }

    val emptyText: KNode = child {
        hasTestTag("favorites_empty")
    }

    val settingsButton: KNode = child {
        hasTestTag("favorites_settings_button")
    }

    val sortButton: KNode = child {
        hasTestTag("favorites_sort_button")
    }

    fun card(id: String): KNode = child {
        hasTestTag("small_card_$id")
    }

    fun cardTitle(id: String): KNode = child {
        useUnmergedTree = true
        hasTestTag("smallcard_title_$id")
    }

    fun cardArtist(id: String): KNode = child {
        useUnmergedTree = true
        hasTestTag("smallcard_artist_$id")
    }

    fun removeFavoriteButton(id: String): KNode = child {
        useUnmergedTree = true
        hasTestTag("smallcard_remove_favorite_$id")
    }
}
