package com.example.feature.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.basepresentation.ui.ReleaseSmallCard
import com.example.core.basepresentation.ui.ReleaseSmallCardMode
import com.example.feature.favorites.R
import com.example.feature.favorites.state.FavoritesScreenState

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    state: FavoritesScreenState,
    callbacks: FavoritesScreenCallbacks,
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current
    val count = state.totalCount

    if (state.favorites.isNotEmpty()) {
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

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            ToolbarActionButtons(callbacks)
            FavoritesHeader(state, count, callbacks)
            FavoritesListSection(state, listState, callbacks)
        }
        if (state.isLoading && state.favorites.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        if (state.hasError) {
            Text(
                text = state.errorProvider(context),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (state.favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_favorites),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

data class FavoritesScreenCallbacks(
    val onRemoveFavorite: (String) -> Unit,
    val onItemClicked: (String) -> Unit,
    val onSettingsClicked: () -> Unit,
    val onSortClicked: () -> Unit,
    val onLoadMore: () -> Unit = {},
    val onGenreClicked: (String?) -> Unit = {},
)

@Composable
private fun ToolbarActionButtons(callbacks: FavoritesScreenCallbacks) {
    Row(horizontalArrangement = Arrangement.End) {
        IconButton(
            modifier =
                Modifier
                    .padding(6.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = CircleShape,
                    ),
            onClick = callbacks.onSettingsClicked,
        ) {
            Icon(
                modifier = Modifier.size(48.dp).padding(6.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            modifier =
                Modifier
                    .padding(6.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = CircleShape,
                    ),
            onClick = callbacks.onSortClicked,
        ) {
            Icon(
                modifier = Modifier.size(48.dp).padding(6.dp),
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FavoritesHeader(
    state: FavoritesScreenState,
    count: Int,
    callbacks: FavoritesScreenCallbacks,
) {
    Column(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
    ) {
        androidx.compose.foundation.Image(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.fav_landscape),
            contentDescription = stringResource(R.string.favorites_image_desc),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
        )
        if (count > 0) {
            Text(
                modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp),
                text = pluralStringResource(R.plurals.favorites_count, count, count),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (state.availableGenres.isNotEmpty()) {
            GenreFilterBar(state, callbacks)
        }
    }
}

@Composable
private fun GenreFilterBar(
    state: FavoritesScreenState,
    callbacks: FavoritesScreenCallbacks,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        filterChip(text = stringResource(R.string.genre_all), isSelected = state.selectedGenre == null) {
            callbacks.onGenreClicked(null)
        }
        state.availableGenres.forEach { genre ->
            filterChip(text = genre, isSelected = genre == state.selectedGenre) {
                callbacks.onGenreClicked(genre)
            }
        }
    }
}

@Composable
private fun filterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    Text(
        text = text,
        modifier =
            Modifier
                .background(color = bgColor, shape = RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 6.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
    )
}

@Composable
private fun FavoritesListSection(
    state: FavoritesScreenState,
    listState: LazyListState,
    callbacks: FavoritesScreenCallbacks,
) {
    if (state.favorites.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            items(items = state.favorites, key = { it.id }) { favorite ->
                ReleaseSmallCard(
                    release = favorite.toReleaseCardState(),
                    mode = ReleaseSmallCardMode.FAVORITES,
                    onRemoveFavorite = callbacks.onRemoveFavorite,
                    onItemClicked = callbacks.onItemClicked,
                )
            }
            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    MaterialTheme {
        FavoritesScreen(
            state =
                FavoritesScreenState(
                    isLoading = false,
                    isLoadingMore = false,
                    hasNextPage = true,
                    hasError = false,
                    errorProvider = { "" },
                    favorites = listOf(),
                    availableGenres = listOf("Rock", "Jazz", "Electronic", "Hip-Hop"),
                    selectedGenre = null,
                ),
            callbacks =
                FavoritesScreenCallbacks(
                    onRemoveFavorite = {},
                    onItemClicked = { },
                    onSortClicked = { },
                    onSettingsClicked = { },
                    onGenreClicked = { },
                ),
        )
    }
}
