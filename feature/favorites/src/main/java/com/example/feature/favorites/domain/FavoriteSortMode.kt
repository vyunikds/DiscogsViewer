package com.example.feature.favorites.domain

import androidx.annotation.StringRes
import com.example.feature.favorites.R

enum class FavoriteSortMode(@StringRes val labelRes: Int) {
    BY_DATE(R.string.sort_by_date),
    BY_ARTIST_TITLE(R.string.sort_by_artist_title),
    BY_RELEASE_TITLE(R.string.sort_by_release_title),
}
