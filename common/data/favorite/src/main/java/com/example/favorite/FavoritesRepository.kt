package com.example.favorite

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun consumeReleaseIds(): Flow<List<String>>
    fun consumeCount(): Flow<Int>
    suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int
    ): List<FavoriteReleaseItem>

    suspend fun addToFavorites(item: FavoriteItem)
    suspend fun removeFromFavorites(releaseId: String)
}
