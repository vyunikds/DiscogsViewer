package com.example.discogsviewer.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class DetailsScreenPO(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DetailsScreenPO>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("details_screen") },
    ) {
    val title: KNode = child {
        useUnmergedTree = true
        hasTestTag("details_title")
    }

    val shareButton: KNode = child {
        hasTestTag("details_share_button")
    }

    val retryButton: KNode = child {
        hasTestTag("details_retry_button")
    }

    val loadingIndicator: KNode = child {
        hasTestTag("details_loading")
    }
}
