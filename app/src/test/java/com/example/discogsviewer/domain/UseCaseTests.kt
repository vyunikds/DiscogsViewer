package com.example.discogsviewer.domain

import com.example.database.dbo.CountryDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseDbo
import com.example.discogsviewer.favorites.domain.FavoriteSortMode
import com.example.discogsviewer.favorites.domain.LoadFavoritesPageUseCaseImpl
import com.example.discogsviewer.favorites.domain.toDataSourceSortMode
import com.example.favorite.DataSourceSortMode
import com.example.favorite.FavoriteItem
import com.example.favorite.FavoriteReleaseItem
import com.example.favorite.FavoritesRepository
import com.example.favorite.ToggleFavoriteUseCaseImpl
import com.example.database.dbo.FullReleaseDbo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UseCaseTests {

    private lateinit var toggleFavoriteUseCase: com.example.favorite.ToggleFavoriteUseCase
    private lateinit var loadFavoritesPageUseCase: com.example.discogsviewer.favorites.domain.LoadFavoritesPageUseCase
    private lateinit var mockFavoritesRepository: FavoritesRepository

    @Before
    fun setup() {
        mockFavoritesRepository = object : FavoritesRepository {
            override fun consumeReleaseIds(): Flow<List<String>> = flowOf(emptyList())
            override fun consumeCount(): Flow<Int> = flowOf(0)
            override fun consumeFavoriteGenres(): Flow<List<String>> = flowOf(emptyList())
            override suspend fun consumePaginated(
                sortMode: DataSourceSortMode,
                limit: Int,
                offset: Int,
                genre: String?,
            ): List<FavoriteReleaseItem> = testPaginatedItems

            override suspend fun addToFavorites(item: FavoriteItem) {
                recordedAddItem = item
            }

            override suspend fun removeFromFavorites(releaseId: String) {
                recordedRemoveReleaseId = releaseId
            }

            override suspend fun getFilteredGenreCount(genre: String): Int = 0
        }

        toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(mockFavoritesRepository)
        loadFavoritesPageUseCase = LoadFavoritesPageUseCaseImpl(mockFavoritesRepository)
    }

    private var recordedAddItem: FavoriteItem? = null
    private var recordedRemoveReleaseId: String? = null
    private var testPaginatedItems: List<FavoriteReleaseItem> = emptyList()

    private fun clearRecordedValues() {
        recordedAddItem = null
        recordedRemoveReleaseId = null
    }

    fun createReleaseDbo(
        id: String = "1",
        artistTitle: String = "Test Artist",
        releaseTitle: String = "Test Release",
        thumb: String = "https://example.com/thumb.jpg",
        coverImage: String = "https://example.com/cover.jpg",
    ) = ReleaseDbo(
        id = id,
        artistTitle = artistTitle,
        releaseTitle = releaseTitle,
        thumb = thumb,
        coverImage = coverImage,
    )

    fun createFullReleaseDbo(
        countries: List<CountryDbo> = listOf(CountryDbo("GB")),
        genres: List<GenreDbo> = listOf(GenreDbo("Rock")),
        release: ReleaseDbo = createReleaseDbo(),
    ) = FullReleaseDbo(
        release = release,
        genresList = genres,
        countriesList = countries,
    )

    fun createFavoriteReleaseItem(
        releaseId: String = "1",
        fullRelease: FullReleaseDbo = createFullReleaseDbo(),
    ) = FavoriteReleaseItem(
        releaseId = releaseId,
        fullRelease = fullRelease,
    )

    // ToggleFavoriteUseCaseImpl tests

    @Test
    fun `ToggleFavoriteUseCase when isFavorite is true calls addToFavorites with correct FavoriteItem`() = runTest {
        clearRecordedValues()
        val releaseId = "abc123"
        val addedAt = 1000L

        toggleFavoriteUseCase(releaseId, addedAt, true)

        val addedItem = recordedAddItem
        assertNotNull(addedItem)
        assertEquals(releaseId, addedItem.releaseId)
        assertEquals(addedAt, addedItem.addedAt)
        assertEquals(null, recordedRemoveReleaseId)
    }

    @Test
    fun `ToggleFavoriteUseCase when isFavorite is false calls removeFromFavorites with correct releaseId`() = runTest {
        clearRecordedValues()
        val releaseId = "xyz789"
        val addedAt = 2000L

        toggleFavoriteUseCase(releaseId, addedAt, false)

        assertEquals(null, recordedAddItem)
        assertEquals(releaseId, recordedRemoveReleaseId)
    }

    // LoadFavoritesPageUseCaseImpl tests

    @Test
    fun `LoadFavoritesPageUseCase returns empty list when repository returns empty`() = runTest {
        testPaginatedItems = emptyList()

        val result = loadFavoritesPageUseCase(FavoriteSortMode.BY_DATE, 20, 0)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `LoadFavoritesPageUseCase maps single item correctly`() = runTest {
        val releaseId = "42"
        val fullRelease = createFullReleaseDbo(
            countries = listOf(CountryDbo("US")),
            genres = listOf(GenreDbo("Jazz"), GenreDbo("Blues")),
            release = createReleaseDbo(
                id = releaseId,
                artistTitle = "Miles Davis",
                releaseTitle = "Kind of Blue",
            ),
        )
        testPaginatedItems = listOf(createFavoriteReleaseItem(releaseId, fullRelease))

        val result = loadFavoritesPageUseCase(FavoriteSortMode.BY_ARTIST_TITLE, 20, 0)

        assertEquals(1, result.size)
        val item = result[0]
        assertEquals(releaseId, item.release.id)
        assertEquals("Miles Davis", item.release.artistTitle)
        assertEquals("Kind of Blue", item.release.releaseTitle)
        assertEquals("US", item.release.country)
        assertEquals(listOf("Jazz", "Blues"), item.release.genre)
        assertTrue(item.isFavorite)
    }

    @Test
    fun `LoadFavoritesPageUseCase maps multiple items correctly`() = runTest {
        val items = listOf(
            createFavoriteReleaseItem(
                releaseId = "1",
                fullRelease = createFullReleaseDbo(
                    countries = listOf(CountryDbo("GB")),
                    genres = listOf(GenreDbo("Rock")),
                    release = createReleaseDbo(
                        id = "1",
                        artistTitle = "Artist A",
                        releaseTitle = "Release A",
                    ),
                ),
            ),
            createFavoriteReleaseItem(
                releaseId = "2",
                fullRelease = createFullReleaseDbo(
                    countries = listOf(CountryDbo("US")),
                    genres = listOf(GenreDbo("Jazz")),
                    release = createReleaseDbo(
                        id = "2",
                        artistTitle = "Artist B",
                        releaseTitle = "Release B",
                    ),
                ),
            ),
        )
        testPaginatedItems = items

        val result = loadFavoritesPageUseCase(FavoriteSortMode.BY_RELEASE_TITLE, 20, 0)

        assertEquals(2, result.size)
        assertEquals("1", result[0].release.id)
        assertEquals("Artist A", result[0].release.artistTitle)
        assertEquals("Release A", result[0].release.releaseTitle)
        assertEquals("GB", result[0].release.country)
        assertEquals(listOf("Rock"), result[0].release.genre)
        assertTrue(result[0].isFavorite)

        assertEquals("2", result[1].release.id)
        assertEquals("Artist B", result[1].release.artistTitle)
        assertEquals("Release B", result[1].release.releaseTitle)
        assertEquals("US", result[1].release.country)
        assertEquals(listOf("Jazz"), result[1].release.genre)
        assertTrue(result[1].isFavorite)
    }

    @Test
    fun `LoadFavoritesPageUseCase isFavorite is always true for all items`() = runTest {
        testPaginatedItems = listOf(
            createFavoriteReleaseItem("1"),
            createFavoriteReleaseItem("2"),
            createFavoriteReleaseItem("3"),
        )

        val result = loadFavoritesPageUseCase(FavoriteSortMode.BY_DATE, 20, 0)

        result.forEach { item ->
            assertTrue(item.isFavorite)
        }
    }

    @Test
    fun `LoadFavoritesPageUseCase country defaults to empty when countriesList is empty`() = runTest {
        testPaginatedItems = listOf(
            createFavoriteReleaseItem(
                releaseId = "99",
                fullRelease = createFullReleaseDbo(
                    countries = emptyList(),
                    genres = listOf(GenreDbo("Electronic")),
                    release = createReleaseDbo(id = "99"),
                ),
            ),
        )

        val result = loadFavoritesPageUseCase(FavoriteSortMode.BY_DATE, 20, 0)

        assertEquals("", result[0].release.country)
    }

    @Test
    fun `FavoriteSortMode toDataSourceSortMode maps BY_DATE correctly`() {
        val result = FavoriteSortMode.BY_DATE.toDataSourceSortMode()
        assertEquals(DataSourceSortMode.BY_DATE, result)
    }

    @Test
    fun `FavoriteSortMode toDataSourceSortMode maps BY_ARTIST_TITLE correctly`() {
        val result = FavoriteSortMode.BY_ARTIST_TITLE.toDataSourceSortMode()
        assertEquals(DataSourceSortMode.BY_ARTIST_TITLE, result)
    }

    @Test
    fun `FavoriteSortMode toDataSourceSortMode maps BY_RELEASE_TITLE correctly`() {
        val result = FavoriteSortMode.BY_RELEASE_TITLE.toDataSourceSortMode()
        assertEquals(DataSourceSortMode.BY_RELEASE_TITLE, result)
    }
}
