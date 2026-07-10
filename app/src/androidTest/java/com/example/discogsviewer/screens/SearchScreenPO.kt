package com.example.discogsviewer.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SearchScreenPO(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SearchScreenPO>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("search_screen") },
    ) {
    val searchField: KNode = child {
        hasTestTag("search_field")
    }

    val historyItem: KNode = child {
        hasTestTag("search_history_item")
    }

    val historyQueryText: KNode = child {
        useUnmergedTree = true
        hasTestTag("search_history_query")
    }

    val historyDeleteButton: KNode = child {
        useUnmergedTree = true
        hasTestTag("search_history_delete")
    }

    val searchLoading: KNode = child {
        hasTestTag("search_loading")
    }

    val emptyHistoryText: KNode = child {
        hasTestTag("search_empty")
    }

    fun searchResultCard(id: String): KNode = child {
        hasTestTag("small_card_$id")
    }
}
