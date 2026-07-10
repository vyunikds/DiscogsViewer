package com.example.discogsviewer.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ReleasesScreenPO(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ReleasesScreenPO>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("releases_screen") },
    ) {
    fun releaseCard(id: String): KNode = child {
        hasTestTag("release_card_$id")
    }

    fun releaseTitle(id: String): KNode = child {
        useUnmergedTree = true
        hasTestTag("release_title_$id")
    }

    fun releaseArtist(id: String): KNode = child {
        useUnmergedTree = true
        hasTestTag("release_artist_$id")
    }

    fun releaseFavorite(id: String): KNode = child {
        useUnmergedTree = true
        hasTestTag("release_favorite_$id")
    }

    val loadingIndicator: KNode = child {
        hasTestTag("releases_loading")
    }

    val emptyText: KNode = child {
        hasTestTag("releases_empty")
    }
}
