package com.example.favorite

import com.example.favorite.DataSourceSortMode
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun consumeAllFavorites(): Flow<List<FavoriteItem>>
    fun consumeReleaseIds(): Flow<List<String>>
    fun consumeCount(): Flow<Int>
    suspend fun consumePaginated(sortMode: DataSourceSortMode, limit: Int, offset: Int): List<FavoriteItem>
    suspend fun addToFavorites(item: FavoriteItem)
    suspend fun removeFromFavorites(releaseId: String)
}