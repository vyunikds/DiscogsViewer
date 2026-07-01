package com.example.favorite

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun consumeReleaseIds(): Flow<List<String>>
    fun consumeCount(): Flow<Int>
    fun consumeFavoriteGenres(): Flow<List<String>>
    suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int,
        genre: String? = null,
    ): List<FavoriteReleaseItem>

    suspend fun getFilteredGenreCount(genre: String): Int
    suspend fun addToFavorites(item: FavoriteItem)
    suspend fun removeFromFavorites(releaseId: String)
}
