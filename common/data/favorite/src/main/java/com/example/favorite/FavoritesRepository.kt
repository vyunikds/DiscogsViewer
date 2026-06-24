package com.example.favorite

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun consumeAllFavorites(): Flow<List<FavoriteItem>>
    fun consumeReleaseIds(): Flow<List<String>>
    fun consumeCount(): Flow<Int>
    fun consumeFavoriteGenres(): Flow<List<String>>
    suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int,
        genre: String? = null,
    ): List<FavoriteItem>

    suspend fun getFilteredGenreCount(genre: String): Int
    suspend fun addToFavorites(item: FavoriteItem)
    suspend fun removeFromFavorites(releaseId: String)
}