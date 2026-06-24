package com.example.discogsviewer.releases.feature.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.discogsviewer.releases.feature.ReleaseState
import com.example.discogsviewer.releases.feature.ReleasesScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleasesScreen(
    modifier: Modifier = Modifier,
    state: ReleasesScreenState,
    onRefresh: () -> Unit,
    onItemClicked: (String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
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
    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                items(state.releasesListState, key = { it.id }) { release ->
                    ReleaseLargeCard(
                        release = release.toReleaseCardState(),
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

            if (state.isLoading && state.releasesListState.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReleasesScreenWithDataPreview() {
    ReleasesScreen(
        state = ReleasesScreenState(
            releasesListState = listOf(
                ReleaseState(
                    id = "0",
                    artistTitle = "Artist Title",
                    releaseTitle = "Release Title",
                    country = "UK",
                    thumb = "https://i.discogs.com/5m6_bBtu4gBfLQSmEr80zoVNXvZGFB8Ld3ajU7_Vkoo/rs:fit/g:sm/q:40/h:150/w:150/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTE1OTk3/NjMyLTE2MDE2MTA1/NTYtMzQwNC5qcGVn.jpeg",
                    isFavorite = true,
                    genre = listOf("Rock"),
                )
            ),
            isLoading = false
        ),
        onRefresh = {},
        onItemClicked = {},
        onErrorShown = {},
        onToggleFavorite = { _, _ -> },
    )
}