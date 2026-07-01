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
    onRemoveFavorite: (String) -> Unit,
    onItemClicked: (String) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    onSettingsClicked: () -> Unit,
    onSortClicked: () -> Unit,
    onLoadMore: () -> Unit = {},
    onGenreClicked: (String?) -> Unit = {},
) {
    val context = LocalContext.current
    val count = state.totalCount

    if (state.favorites.isNotEmpty()) {
        LaunchedEffect(listState, state.hasNextPage, state.isLoadingMore) {
            snapshotFlow {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                val totalCount = listState.layoutInfo.totalItemsCount
                val lastVisible = visibleItems.lastOrNull()?.index ?: 0
                lastVisible >= totalCount - 3 && state.hasNextPage && !state.isLoadingMore
            }.collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Row(
                modifier = Modifier
                    .align(Alignment.End),
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    onClick = onSettingsClicked,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(6.dp),
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    onClick = onSortClicked,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(6.dp),
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
            ) {
                androidx.compose.foundation.Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    painter = androidx.compose.ui.res.painterResource(R.drawable.fav_landscape),
                    contentDescription = stringResource(R.string.favorites_image_desc),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                )
                if (count > 0) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 6.dp),
                        text = pluralStringResource(R.plurals.favorites_count, count, count),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (state.availableGenres.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.genre_all),
                            modifier = Modifier
                                .background(
                                    color = if (state.selectedGenre == null)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onGenreClicked(null) }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.selectedGenre == null)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSecondary
                        )
                        state.availableGenres.forEach { genre ->
                            val isSelected = genre == state.selectedGenre
                            Text(
                                text = genre,
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onGenreClicked(genre) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }

            if (state.favorites.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                ) {
                    items(
                        items = state.favorites,
                        key = { it.id }
                    ) { favorite ->
                        ReleaseSmallCard(
                            release = favorite.toReleaseCardState(),
                            mode = ReleaseSmallCardMode.FAVORITES,
                            onRemoveFavorite = onRemoveFavorite,
                            onItemClicked = onItemClicked,
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }

        when {
            state.isLoading && state.favorites.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.hasError -> {
                Text(
                    text = state.errorProvider(context),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.favorites.isEmpty() -> {
                Text(
                    text = stringResource(R.string.empty_favorites),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    MaterialTheme {
        FavoritesScreen(
            state = FavoritesScreenState(
                isLoading = false,
                isLoadingMore = false,
                hasNextPage = true,
                hasError = false,
                errorProvider = { "" },
                favorites = listOf(),
                availableGenres = listOf("Rock", "Jazz", "Electronic", "Hip-Hop"),
                selectedGenre = null,
            ),
            onRemoveFavorite = {},
            onItemClicked = { },
            onSortClicked = { },
            onSettingsClicked = { },
            onGenreClicked = { },
        )
    }
}
