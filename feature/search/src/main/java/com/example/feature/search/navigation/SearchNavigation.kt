package com.example.feature.search.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.search.state.ReleaseSearchViewModel
import com.example.feature.search.state.ReleasesSearchScreenState
import com.example.feature.search.ui.SearchScreen

@Composable
fun SearchScreenRoute(
    listState: LazyListState,
    onItemClicked: (String) -> Unit,
    onScrollToTopProvider: (() -> Unit) -> Unit = {},
) {
    val viewModel: ReleaseSearchViewModel = hiltViewModel()
    val state: ReleasesSearchScreenState by viewModel.state.collectAsStateWithLifecycle()

    val scrollToTop: () -> Unit = { listState.requestScrollToItem(0) }
    val currentScrollToTop by rememberUpdatedState(scrollToTop)
    SideEffect {
        onScrollToTopProvider(currentScrollToTop)
    }

    SearchScreen(
        state = state,
        listState = listState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchConfirmed = viewModel::confirmQuery,
        onHistoryItemClicked = { query ->
            viewModel.onSearchQueryChanged(query)
            viewModel.confirmQuery(query)
        },
        onHistoryItemDeleted = viewModel::removeHistoryItem,
        onItemClicked = { releaseId ->
            if (state.searchQuery.isNotBlank()) {
                viewModel.confirmQuery(state.searchQuery)
            }
            onItemClicked(releaseId)
        },
        onToggleFavorite = viewModel::onToggleFavorite,
        onClearHistory = viewModel::clearHistory,
        onErrorShown = viewModel::errorHasShown,
        onLoadMore = viewModel::loadMore,
    )
}
