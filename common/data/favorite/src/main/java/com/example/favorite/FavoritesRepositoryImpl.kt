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

    override suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int
    ): List<FavoriteReleaseItem> {
        val fullReleases = localDataSource.consumePaginated(sortMode, limit, offset)
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
