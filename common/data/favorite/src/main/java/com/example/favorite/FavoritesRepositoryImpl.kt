package com.example.favorite

import com.example.database.dbo.FavoriteDbo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val localDataSource: FavoritesLocalDataSource,
) : FavoritesRepository {
    override fun consumeReleaseIds(): Flow<List<String>> =
        localDataSource.consumeReleaseIds()

    override fun consumeCount(): Flow<Int> =
        localDataSource.consumeCount()

    override fun consumeFavoriteGenres(): Flow<List<String>> =
        localDataSource.consumeFavoriteGenres()

    override suspend fun getFilteredGenreCount(genre: String): Int =
        localDataSource.getFilteredGenreCount(genre)

    override suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int,
        genre: String?,
    ): List<FavoriteReleaseItem> {
        val fullReleases = localDataSource.consumePaginated(sortMode, limit, offset, genre)
        return fullReleases.map { fullRelease ->
            FavoriteReleaseItem(
                releaseId = fullRelease.release.id,
                fullRelease = fullRelease,
            )
        }
    }

    override suspend fun addToFavorites(item: FavoriteItem) {
        localDataSource.add(
            FavoriteDbo(
                releaseId = item.releaseId,
                addedAt = item.addedAt,
            )
        )
    }

    override suspend fun removeFromFavorites(releaseId: String) {
        localDataSource.remove(releaseId)
    }
}
