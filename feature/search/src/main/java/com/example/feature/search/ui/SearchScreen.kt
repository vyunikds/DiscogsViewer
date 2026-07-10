package com.example.feature.search.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.basepresentation.ui.ReleaseSmallCard
import com.example.core.basepresentation.ui.ReleaseSmallCardMode
import com.example.feature.search.R
import com.example.feature.search.state.ReleasesSearchScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: ReleasesSearchScreenState,
    callbacks: SearchScreenCallbacks,
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current

    if (state.hasError) {
        LaunchedEffect(state.hasError) {
            Toast.makeText(context, state.errorProvider(context), Toast.LENGTH_SHORT).show()
            callbacks.onErrorShown()
        }
    }

    var searchText by remember(state.searchQuery) { mutableStateOf(state.searchQuery) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.searchQuery) {
        if (state.searchQuery != searchText) {
            searchText = state.searchQuery
        }
    }

    if (state.releasesSearchListState.isNotEmpty()) {
        LaunchedEffect(listState, state.hasNextPage, state.isLoadingMore) {
            snapshotFlow {
                val visible = listState.layoutInfo.visibleItemsInfo
                val total = listState.layoutInfo.totalItemsCount
                val last = visible.lastOrNull()?.index ?: 0
                last >= total - 3 && state.hasNextPage && !state.isLoadingMore
            }.collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    callbacks.onLoadMore()
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("search_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchField(
                searchText = searchText,
                onTextChange = {
                    searchText = it
                    callbacks.onSearchQueryChanged(it)
                },
                onClear = {
                    searchText = ""
                    callbacks.onSearchQueryChanged("")
                },
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    callbacks.onSearchConfirmed(searchText)
                },
            )
            SearchContentArea(state, callbacks, listState)
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun SearchContentArea(
    state: ReleasesSearchScreenState,
    callbacks: SearchScreenCallbacks,
    listState: LazyListState,
) {
    val showHistory = state.searchQuery.isBlank() && state.releasesSearchListState.isEmpty()
    Box(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            if (showHistory && state.searchHistory.isNotEmpty()) {
                items(items = state.searchHistory, key = { it }) { query ->
                    SearchHistoryItem(
                        query = query,
                        onClick = { callbacks.onHistoryItemClicked(query) },
                        onDelete = { callbacks.onHistoryItemDeleted(query) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f),
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.clear_all_history),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable(onClick = callbacks.onClearHistory)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        color = Color.Gray,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f),
                    )
                }
            }
            items(items = state.releasesSearchListState, key = { it.id }) { release ->
                ReleaseSmallCard(
                    release = release.toReleaseCardState(),
                    mode = ReleaseSmallCardMode.TOGGLE_FAVORITE,
                    onToggleFavorite = callbacks.onToggleFavorite,
                    onItemClicked = callbacks.onItemClicked,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.5f),
                )
            }
            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        if (state.isLoading && state.releasesSearchListState.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("search_loading"),
            )
        }
        if (showHistory && state.searchHistory.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_search_history),
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("search_empty"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

data class SearchScreenCallbacks(
    val onSearchQueryChanged: (String) -> Unit,
    val onSearchConfirmed: (String) -> Unit,
    val onHistoryItemClicked: (String) -> Unit,
    val onHistoryItemDeleted: (String) -> Unit,
    val onItemClicked: (String) -> Unit,
    val onToggleFavorite: (String, Boolean) -> Unit,
    val onClearHistory: () -> Unit,
    val onErrorShown: () -> Unit,
    val onLoadMore: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
    searchText: String,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
) {
    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = SearchBarDefaults.colors(dividerColor = Color.Transparent),
        tonalElevation = 0.dp,
        inputField = {
            TextField(
                modifier = Modifier.testTag("search_field"),
                value = searchText,
                onValueChange = onTextChange,
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.clear),
                            )
                        }
                    }
                },
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors =
                    androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
            )
        },
        expanded = false,
        onExpandedChange = {},
    ) {}
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen(
            state =
                ReleasesSearchScreenState(
                    searchQuery = "",
                    releasesSearchListState = emptyList(),
                    searchHistory = listOf("Pink Floyd", "Metallica", "Daft Punk"),
                    isLoading = false,
                    hasError = false,
                    isLoadingMore = false,
                    hasNextPage = true,
                ),
            callbacks =
                SearchScreenCallbacks(
                    onSearchQueryChanged = {},
                    onSearchConfirmed = {},
                    onHistoryItemClicked = {},
                    onHistoryItemDeleted = {},
                    onItemClicked = {},
                    onToggleFavorite = { _, _ -> },
                    onClearHistory = {},
                    onErrorShown = {},
                    onLoadMore = {},
                ),
        )
    }
}

@Preview(showBackground = true, name = "Empty History")
@Composable
private fun SearchScreenEmptyHistoryPreview() {
    MaterialTheme {
        SearchScreen(
            state =
                ReleasesSearchScreenState(
                    searchQuery = "",
                    releasesSearchListState = emptyList(),
                    searchHistory = emptyList(),
                    isLoading = false,
                    hasError = false,
                    isLoadingMore = false,
                    hasNextPage = true,
                ),
            callbacks =
                SearchScreenCallbacks(
                    onSearchQueryChanged = {},
                    onSearchConfirmed = {},
                    onHistoryItemClicked = {},
                    onHistoryItemDeleted = {},
                    onItemClicked = {},
                    onToggleFavorite = { _, _ -> },
                    onClearHistory = {},
                    onErrorShown = {},
                    onLoadMore = {},
                ),
        )
    }
}
