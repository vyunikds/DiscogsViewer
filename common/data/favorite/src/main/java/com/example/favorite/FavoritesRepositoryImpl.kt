package com.example.favorite

import com.example.database.dbo.FavoriteDbo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val localDataSource: FavoritesLocalDataSource,
    private val mapper: FavoriteDboMapper,
) : FavoritesRepository {
    override fun consumeAllFavorites(): Flow<List<FavoriteItem>> =
        localDataSource.consumeAll().map { list -> list.map(mapper::toDomain) }

    override fun consumeReleaseIds(): Flow<List<String>> =
        localDataSource.consumeReleaseIds()

    override fun consumeCount(): Flow<Int> =
        localDataSource.consumeCount()

    override suspend fun consumePaginated(sortMode: DataSourceSortMode, limit: Int, offset: Int): List<FavoriteItem> {
        val dbos = localDataSource.consumePaginated(sortMode, limit, offset)
        return dbos.map(mapper::toDomain)
    }

    override suspend fun addToFavorites(item: FavoriteItem) {
        localDataSource.add(toDbo(item))
    }

    override suspend fun removeFromFavorites(releaseId: String) {
        localDataSource.remove(releaseId)
    }

    private fun toDbo(item: FavoriteItem): FavoriteDbo = FavoriteDbo(
        releaseId = item.releaseId,
        artistTitle = item.artistTitle,
        releaseTitle = item.releaseTitle,
        country = item.country,
        genres = item.genres,
        thumb = item.thumb,
        coverImage = item.coverImage,
        communityHave = item.communityHave,
        communityWant = item.communityWant,
        addedAt = item.addedAt,
    )
}
