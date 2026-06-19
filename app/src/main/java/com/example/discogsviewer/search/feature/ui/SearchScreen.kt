package com.example.discogsviewer.search.feature.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.discogsviewer.R
import com.example.discogsviewer.search.feature.ReleaseSearchState
import com.example.discogsviewer.search.feature.ReleasesSearchScreenState
import com.example.discogsviewer.ui.common.ReleaseSmallCard
import com.example.discogsviewer.ui.common.ReleaseSmallCardMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: ReleasesSearchScreenState,
    onRefresh: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchConfirmed: (String) -> Unit,
    onHistoryItemClicked: (String) -> Unit,
    onHistoryItemDeleted: (String) -> Unit,
    onItemClicked: (String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onErrorShown: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current

    if (state.hasError) {
        LaunchedEffect(state.hasError) {
            Toast.makeText(context, state.errorProvider(context), Toast.LENGTH_SHORT).show()
            onErrorShown()
        }
    }

    var searchText by remember(state.searchQuery) { mutableStateOf(state.searchQuery) }
    var isActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.searchQuery) {
        if (state.searchQuery != searchText) {
            searchText = state.searchQuery
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchText,
            onQueryChange = {
                searchText = it
                onSearchQueryChanged(it)
            },
            onSearch = {
                focusManager.clearFocus()
                onSearchConfirmed(it)
                onSearchQueryChanged(it)
            },
            active = isActive,
//            onActiveChange = { isActive = it },
            onActiveChange = { },
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                        onSearchQueryChanged("")
                    }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
        }
        val showHistory = state.searchQuery.isBlank() && state.releasesSearchListState.isEmpty()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                if (showHistory && state.searchHistory.isNotEmpty()) {
                    items(
                        items = state.searchHistory,
                        key = { it },
                    ) { query ->
                        SearchHistoryItem(
                            query = query,
                            onClick = { onHistoryItemClicked(query) },
                            onDelete = { onHistoryItemDeleted(query) },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.clear_all_history),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onClearHistory)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            color = Color.Gray,
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
                items(
                    items = state.releasesSearchListState,
                    key = { it.id },
                ) { release ->
                    ReleaseSmallCard(
                        release = release.toReleaseCardState(),
                        mode = ReleaseSmallCardMode.TOGGLE_FAVORITE,
                        onToggleFavorite = onToggleFavorite,
                        onItemClicked = onItemClicked,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }

            if (state.isLoading && state.releasesSearchListState.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        state = ReleasesSearchScreenState(
            searchQuery = "",
            releasesSearchListState = emptyList(),
            searchHistory = listOf("Pink Floyd", "Metallica", "Daft Punk"),
            isLoading = false,
            hasError = false,
        ),
        onRefresh = {},
        onSearchQueryChanged = {},
        onSearchConfirmed = {},
        onHistoryItemClicked = {},
        onHistoryItemDeleted = {},
        onItemClicked = {},
        onToggleFavorite = { _, _ -> },
        onClearHistory = {},
        onErrorShown = {},
    )
}
