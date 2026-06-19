package com.example.discogsviewer.details.feature

import android.content.Context
import androidx.compose.runtime.Immutable

typealias ErrorProvider = (Context) -> String

@Immutable
data class ReleaseDetailsScreenState(
    val isLoading: Boolean = false,
    val detailsState: ReleaseDetailsState = ReleaseDetailsState(),
    val hasError: Boolean = false,
    val errorProvider: ErrorProvider = { "" },
)

data class ReleaseDetailsState(
    val id: Int = 0,
    val releaseTitle: String = "",
    val artistTitle: String = "",
    val want: Int = 0,
    val have: Int = 0,
    val country: String = "",
    val genres: List<String> = emptyList(),
    val coverImage: String = "",
    val isFavorite: Boolean = false,
)
