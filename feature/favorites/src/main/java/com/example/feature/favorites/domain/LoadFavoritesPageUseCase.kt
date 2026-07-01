package com.example.feature.favorites.domain

import com.example.favorite.DataSourceSortMode

interface LoadFavoritesPageUseCase {
    suspend operator fun invoke(
        sortMode: FavoriteSortMode,
        limit: Int,
        offset: Int,
        genre: String? = null,
    ): List<ReleaseWithFavorite>
}

internal fun FavoriteSortMode.toDataSourceSortMode(): DataSourceSortMode =
    when (this) {
        FavoriteSortMode.BY_DATE -> DataSourceSortMode.BY_DATE
        FavoriteSortMode.BY_ARTIST_TITLE -> DataSourceSortMode.BY_ARTIST_TITLE
        FavoriteSortMode.BY_RELEASE_TITLE -> DataSourceSortMode.BY_RELEASE_TITLE
    }
