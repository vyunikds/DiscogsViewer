package com.example.discogsviewer.search.feature

import android.content.Context
import androidx.compose.runtime.Immutable
import com.example.discogsviewer.ui.common.ReleaseCardState

typealias ErrorProvider = (Context) -> String

@Immutable
data class ReleasesSearchScreenState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val releasesSearchListState: List<ReleaseSearchState> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val hasError: Boolean = false,
    val errorProvider: ErrorProvider = { "" },
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = true,
)

data class ReleaseSearchState(
    val id: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genre: List<String>,
    val thumb: String,
    val coverImage: String = "",
    val isFavorite: Boolean,
) {
    fun toReleaseCardState(): ReleaseCardState = ReleaseCardState(
        id = id,
        artistTitle = artistTitle,
        releaseTitle = releaseTitle,
        country = country,
        genre = genre,
        thumb = thumb,
        coverImage = coverImage,
        isFavorite = isFavorite,
    )
}
