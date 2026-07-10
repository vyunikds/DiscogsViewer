package com.example.discogsviewer.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class BottomBarPO(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<BottomBarPO>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("bottom_nav_bar") },
    ) {
    val releasesNavItem: KNode = child {
        hasTestTag("bottom_nav_releases_screen")
    }

    val searchNavItem: KNode = child {
        hasTestTag("bottom_nav_search")
    }

    val favoritesNavItem: KNode = child {
        hasTestTag("bottom_nav_favorites")
    }
}
