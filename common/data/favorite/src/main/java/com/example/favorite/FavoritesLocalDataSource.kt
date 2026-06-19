package com.example.favorite

import com.example.database.dao.FavoritesDao
import com.example.database.dbo.FavoriteDbo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

enum class DataSourceSortMode {
    BY_DATE,
    BY_ARTIST_TITLE,
    BY_RELEASE_TITLE,
}

@Singleton
class FavoritesLocalDataSource @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun consumeAll(): Flow<List<FavoriteDbo>> = favoritesDao.getAll()

    fun consumeReleaseIds(): Flow<List<String>> = favoritesDao.getReleaseIds()

    fun consumeCount(): Flow<Int> = favoritesDao.getCount()

    suspend fun consumePaginated(sortMode: DataSourceSortMode, limit: Int, offset: Int): List<FavoriteDbo> =
        when (sortMode) {
            DataSourceSortMode.BY_DATE -> favoritesDao.getPaginatedByDate(limit, offset)
            DataSourceSortMode.BY_ARTIST_TITLE -> favoritesDao.getPaginatedByArtistTitle(limit, offset)
            DataSourceSortMode.BY_RELEASE_TITLE -> favoritesDao.getPaginatedByReleaseTitle(limit, offset)
        }

    suspend fun add(favorite: FavoriteDbo) = favoritesDao.insert(favorite)

    suspend fun remove(releaseId: String) = favoritesDao.delete(releaseId)
}