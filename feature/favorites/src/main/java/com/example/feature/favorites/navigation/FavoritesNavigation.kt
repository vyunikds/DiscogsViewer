package com.example.feature.favorites.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.favorites.FavoritesViewModel
import com.example.feature.favorites.state.FavoritesScreenState
import com.example.feature.favorites.ui.FavoriteSortBottomSheet
import com.example.feature.favorites.ui.FavoritesScreen

@Composable
fun FavoritesScreenRoute(
    listState: LazyListState,
    onItemClicked: (String) -> Unit,
    onSettingsClicked: () -> Unit,
    onScrollToTopProvider: (() -> Unit) -> Unit = {},
) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val state: FavoritesScreenState by viewModel.state.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }

    val scrollToTop: () -> Unit = { listState.requestScrollToItem(0) }
    val currentScrollToTop by rememberUpdatedState(scrollToTop)
    SideEffect {
        onScrollToTopProvider(currentScrollToTop)
    }

    if (showSettings) {
        FavoriteSortBottomSheet(
            currentMode = state.currentSortMode,
            onDismiss = { showSettings = false },
            onSelect = { mode ->
                showSettings = false
                viewModel.setSortMode(mode)
                scrollToTop()
            },
        )
    }

    FavoritesScreen(
        state = state,
        listState = listState,
        onRemoveFavorite = viewModel::onRemoveFavorite,
        onItemClicked = onItemClicked,
        onSettingsClicked = onSettingsClicked,
        onSortClicked = { showSettings = true },
        onLoadMore = viewModel::loadMore,
        onGenreClicked = { genre ->
            viewModel.setGenreFilter(genre)
            scrollToTop()
        },
    )
}
