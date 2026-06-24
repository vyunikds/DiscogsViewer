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
        favoritesDao.getAll().map { all: List<FavoriteDbo> ->
            all.flatMap { item -> item.genres }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { (_, count) -> count }
                .map { (genre, _) -> genre }
        }

    suspend fun getFilteredGenreCount(genre: String): Int {
        val all = favoritesDao.getAll().first()
        return all.count { item -> genre in item.genres }
    }

    suspend fun consumePaginated(
        sortMode: DataSourceSortMode,
        limit: Int,
        offset: Int,
        genre: String? = null,
    ): List<FullReleaseDbo> {
        if (genre != null) {
            val filtered = favoritesDao.getAll()
                .first()
                .filter { item -> genre in item.genres }

            val sorted: List<FavoriteDbo> = when (sortMode) {
                DataSourceSortMode.BY_DATE -> {
                    filtered.sortedByDescending { item: FavoriteDbo -> item.addedAt }
                }
                DataSourceSortMode.BY_ARTIST_TITLE -> {
                    val allArtists = filtered.map { item: FavoriteDbo -> item.artistTitle.lowercase() }.distinct().sorted()
                    val allTitles = filtered.map { item: FavoriteDbo -> item.releaseTitle.lowercase() }.distinct().sorted()
                    filtered.sortedWith(Comparator<FavoriteDbo> { a, b ->
                        val artistCmp = allArtists.indexOf(a.artistTitle.lowercase()).compareTo(allArtists.indexOf(b.artistTitle.lowercase()))
                        if (artistCmp != 0) artistCmp
                        else allTitles.indexOf(a.releaseTitle.lowercase()).compareTo(allTitles.indexOf(b.releaseTitle.lowercase()))
                    })
                }
                DataSourceSortMode.BY_RELEASE_TITLE -> {
                    val allTitles = filtered.map { item: FavoriteDbo -> item.releaseTitle.lowercase() }.distinct().sorted()
                    val allArtists = filtered.map { item: FavoriteDbo -> item.artistTitle.lowercase() }.distinct().sorted()
                    filtered.sortedWith(Comparator<FavoriteDbo> { a, b ->
                        val titleCmp = allTitles.indexOf(a.releaseTitle.lowercase()).compareTo(allTitles.indexOf(b.releaseTitle.lowercase()))
                        if (titleCmp != 0) titleCmp
                        else allArtists.indexOf(a.artistTitle.lowercase()).compareTo(allArtists.indexOf(b.artistTitle.lowercase()))
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
