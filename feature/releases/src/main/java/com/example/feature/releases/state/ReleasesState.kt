package com.example.feature.releases.state

import android.content.Context
import androidx.compose.runtime.Immutable
import com.example.core.basepresentation.ui.ReleaseCardState

typealias ErrorProvider = (Context) -> String

@Immutable
data class ReleasesScreenState(
    val isLoading: Boolean = false,
    val releasesListState: List<ReleaseState> = emptyList(),
    val hasError: Boolean = false,
    val errorProvider: ErrorProvider = { "" },
    val isInitialized: Boolean = false,
)

data class ReleaseState(
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
