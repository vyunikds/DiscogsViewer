package com.example.favorite

import com.example.database.dao.FavoritesDao
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.FullReleaseDbo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    fun consumeReleaseIds(): Flow<List<String>> = favoritesDao.getReleaseIds()

    fun consumeCount(): Flow<Int> = favoritesDao.getCount()

    fun consumeFavoriteGenres(): Flow<List<String>> =
        favoritesDao.getAllFull().map { all: List<FullReleaseDbo> ->
            all.flatMap { it.genresList.map { g -> g.genre } }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { (_, count) -> count }
                .map { (genre, _) -> genre }
        }

    suspend fun getFilteredGenreCount(genre: String): Int {
        val all = favoritesDao.getAllFull().first()
        return all.count { it.genresList.any { g -> g.genre == genre } }
    }

    suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int,
        genre: String? = null,
    ): List<FullReleaseDbo> {
        if (genre != null) {
            val filtered: List<FullReleaseDbo> = favoritesDao.getAllFull()
                .first()
                .filter { it.genresList.any { g -> g.genre == genre } }

            val sorted: List<FullReleaseDbo> = when (sortMode) {
                DataSourceSortMode.BY_DATE -> {
                    val favAddedAt = favoritesDao.getAll().first().associate { it.releaseId to it.addedAt }
                    filtered.sortedByDescending { favAddedAt[it.release.id] ?: 0L }
                }
                DataSourceSortMode.BY_ARTIST_TITLE -> {
                    val allArtists = filtered.map { it.release.artistTitle.lowercase() }.distinct().sorted()
                    val allTitles = filtered.map { it.release.releaseTitle.lowercase() }.distinct().sorted()
                    filtered.sortedWith(compareBy<FullReleaseDbo> {
                        allArtists.indexOf(it.release.artistTitle.lowercase())
                    }.thenBy {
                        allTitles.indexOf(it.release.releaseTitle.lowercase())
                    })
                }
                DataSourceSortMode.BY_RELEASE_TITLE -> {
                    val allTitles = filtered.map { it.release.releaseTitle.lowercase() }.distinct().sorted()
                    val allArtists = filtered.map { it.release.artistTitle.lowercase() }.distinct().sorted()
                    filtered.sortedWith(compareBy<FullReleaseDbo> {
                        allTitles.indexOf(it.release.releaseTitle.lowercase())
                    }.thenBy {
                        allArtists.indexOf(it.release.artistTitle.lowercase())
                    })
                }
            }
            return sorted.drop(offset).take(limit)
        }
        return when (sortMode) {
            DataSourceSortMode.BY_DATE -> favoritesDao.getPaginatedByDate(limit, offset)
            DataSourceSortMode.BY_ARTIST_TITLE -> favoritesDao.getPaginatedByArtistTitle(limit, offset)
            DataSourceSortMode.BY_RELEASE_TITLE -> favoritesDao.getPaginatedByReleaseTitle(limit, offset)
        }
    }

    suspend fun add(favorite: FavoriteDbo) = favoritesDao.insert(favorite)

    suspend fun remove(releaseId: String) = favoritesDao.delete(releaseId)
}
