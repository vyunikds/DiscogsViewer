package com.example.discogsviewer.favorites.feature

import android.content.Context
import androidx.compose.runtime.Immutable
import com.example.discogsviewer.favorites.domain.FavoriteSortMode
import com.example.discogsviewer.ui.common.ReleaseCardState

typealias ErrorProvider = (Context) -> String

@Immutable
data class FavoritesScreenState(
    val favorites: List<FavoriteReleasesState> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = true,
    val hasError: Boolean = false,
    val errorProvider: ErrorProvider = { "" },
    val currentSortMode: FavoriteSortMode = FavoriteSortMode.BY_DATE,
)

data class FavoriteReleasesState(
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
