package com.example.feature.releases.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.releases.ReleasesViewModel
import com.example.feature.releases.state.ReleasesScreenState
import com.example.feature.releases.ui.ReleasesScreen

@Composable
fun ReleasesScreenRoute(
    listState: LazyListState,
    onItemClicked: (String) -> Unit,
    onScrollToTopProvider: (() -> Unit) -> Unit = {},
) {
    val viewModel: ReleasesViewModel = hiltViewModel()
    val state: ReleasesScreenState by viewModel.state.collectAsStateWithLifecycle()

    val scrollToTop: () -> Unit = { listState.requestScrollToItem(0) }
    val currentScrollToTop by rememberUpdatedState(scrollToTop)
    SideEffect {
        onScrollToTopProvider(currentScrollToTop)
    }

    ReleasesScreen(
        state = state,
        listState = listState,
        onRefresh = viewModel::refresh,
        onItemClicked = onItemClicked,
        onToggleFavorite = viewModel::onToggleFavorite,
        onErrorShown = viewModel::errorHasShown,
    )
}
