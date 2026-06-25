package com.example.discogsviewer.feature

import com.example.discogsviewer.details.domain.ReleaseDetails
import com.example.discogsviewer.details.feature.ReleaseDetailsStateFactory
import com.example.discogsviewer.favorites.feature.FavoritesStateFactory
import com.example.discogsviewer.releases.domain.Release
import com.example.discogsviewer.releases.domain.ReleaseWithFavorite
import com.example.discogsviewer.releases.feature.ReleasesStateFactory
import com.example.discogsviewer.search.domain.ReleaseSearch
import com.example.discogsviewer.search.domain.ReleaseSearchWithFavorite
import com.example.discogsviewer.search.feature.ReleasesSearchStateFactory
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StateFactoryTest {

    private lateinit var releasesStateFactory: ReleasesStateFactory
    private lateinit var releasesSearchStateFactory: ReleasesSearchStateFactory
    private lateinit var favoritesStateFactory: FavoritesStateFactory
    private lateinit var releaseDetailsStateFactory: ReleaseDetailsStateFactory

    @Before
    fun setup() {
        releasesStateFactory = ReleasesStateFactory()
        releasesSearchStateFactory = ReleasesSearchStateFactory()
        favoritesStateFactory = FavoritesStateFactory()
        releaseDetailsStateFactory = ReleaseDetailsStateFactory()
    }

    // ReleasesStateFactory tests

    @Test
    fun `ReleasesStateFactory - all fields mapped correctly`() {
        val releaseWithFavorite = createReleaseWithFavorite(
            release = createRelease(
                id = "12345",
                artistTitle = "Test Artist",
                releaseTitle = "Test Release",
                country = "US",
                genre = listOf("Rock", "Pop"),
                thumb = "https://example.com/thumb.jpg",
                coverImage = "https://example.com/cover.jpg"
            ),
            isFavorite = true
        )

        val result = releasesStateFactory.create(releaseWithFavorite)

        assertEquals("12345", result.id)
        assertEquals("Test Artist", result.artistTitle)
        assertEquals("Test Release", result.releaseTitle)
        assertEquals("US", result.country)
        assertEquals(listOf("Rock", "Pop"), result.genre)
        assertEquals("https://example.com/thumb.jpg", result.thumb)
        assertEquals("https://example.com/cover.jpg", result.coverImage)
        assertTrue(result.isFavorite)
    }

    @Test
    fun `ReleasesStateFactory - isFavorite maps from ReleaseWithFavorite wrapper`() {
        val releaseWithFavorite = createReleaseWithFavorite(isFavorite = false)

        val result = releasesStateFactory.create(releaseWithFavorite)

        assertFalse(result.isFavorite)
    }

    @Test
    fun `ReleasesStateFactory - empty genre and coverImage handled correctly`() {
        val releaseWithFavorite = createReleaseWithFavorite(
            release = createRelease(
                genre = emptyList(),
                coverImage = ""
            )
        )

        val result = releasesStateFactory.create(releaseWithFavorite)

        assertEquals(emptyList<String>(), result.genre)
        assertEquals("", result.coverImage)
    }

    // ReleasesSearchStateFactory tests

    @Test
    fun `ReleasesSearchStateFactory - all fields mapped correctly`() {
        val releaseSearchWithFavorite = createReleaseSearchWithFavorite(
            release = createReleaseSearch(
                id = "67890",
                artistTitle = "Search Artist",
                releaseTitle = "Search Release",
                country = "UK",
                genre = listOf("Electronic", "Ambient"),
                thumb = "https://example.com/search-thumb.jpg"
            ),
            isFavorite = true
        )

        val result = releasesSearchStateFactory.create(releaseSearchWithFavorite)

        assertEquals("67890", result.id)
        assertEquals("Search Artist", result.artistTitle)
        assertEquals("Search Release", result.releaseTitle)
        assertEquals("UK", result.country)
        assertEquals(listOf("Electronic", "Ambient"), result.genre)
        assertEquals("https://example.com/search-thumb.jpg", result.thumb)
        assertTrue(result.isFavorite)
    }

    @Test
    fun `ReleasesSearchStateFactory - isFavorite maps from ReleaseSearchWithFavorite wrapper`() {
        val releaseSearchWithFavorite = createReleaseSearchWithFavorite(isFavorite = false)

        val result = releasesSearchStateFactory.create(releaseSearchWithFavorite)

        assertFalse(result.isFavorite)
    }

    @Test
    fun `ReleasesSearchStateFactory - empty genre handled correctly`() {
        val releaseSearchWithFavorite = createReleaseSearchWithFavorite(
            release = createReleaseSearch(genre = emptyList())
        )

        val result = releasesSearchStateFactory.create(releaseSearchWithFavorite)

        assertEquals(emptyList<String>(), result.genre)
    }

    @Test
    fun `ReleasesSearchStateFactory - default coverImage value`() {
        val releaseSearchWithFavorite = createReleaseSearchWithFavorite()

        val result = releasesSearchStateFactory.create(releaseSearchWithFavorite)

        assertEquals("", result.coverImage)
    }

    // FavoritesStateFactory tests

    @Test
    fun `FavoritesStateFactory - single item mapped correctly`() {
        val favorites = listOf(
            createReleaseWithFavorite(
                release = createRelease(
                    id = "11111",
                    artistTitle = "Fav Artist",
                    releaseTitle = "Fav Release",
                    country = "DE",
                    genre = listOf("Jazz"),
                    thumb = "https://example.com/fav-thumb.jpg",
                    coverImage = "https://example.com/fav-cover.jpg"
                ),
                isFavorite = true
            )
        )

        val result = favoritesStateFactory.create(favorites)

        assertEquals(1, result.size)
        assertEquals("11111", result[0].id)
        assertEquals("Fav Artist", result[0].artistTitle)
        assertEquals("Fav Release", result[0].releaseTitle)
        assertEquals("DE", result[0].country)
        assertEquals(listOf("Jazz"), result[0].genre)
        assertEquals("https://example.com/fav-thumb.jpg", result[0].thumb)
        assertEquals("https://example.com/fav-cover.jpg", result[0].coverImage)
        assertTrue(result[0].isFavorite)
    }

    @Test
    fun `FavoritesStateFactory - multiple items mapped correctly`() {
        val favorites = listOf(
            createReleaseWithFavorite(
                release = createRelease(id = "1", artistTitle = "Artist1", releaseTitle = "Release1"),
                isFavorite = true
            ),
            createReleaseWithFavorite(
                release = createRelease(id = "2", artistTitle = "Artist2", releaseTitle = "Release2"),
                isFavorite = false
            ),
            createReleaseWithFavorite(
                release = createRelease(id = "3", artistTitle = "Artist3", releaseTitle = "Release3"),
                isFavorite = true
            )
        )

        val result = favoritesStateFactory.create(favorites)

        assertEquals(3, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Artist1", result[0].artistTitle)
        assertEquals("Release1", result[0].releaseTitle)
        assertTrue(result[0].isFavorite)

        assertEquals("2", result[1].id)
        assertEquals("Artist2", result[1].artistTitle)
        assertEquals("Release2", result[1].releaseTitle)
        assertFalse(result[1].isFavorite)

        assertEquals("3", result[2].id)
        assertEquals("Artist3", result[2].artistTitle)
        assertEquals("Release3", result[2].releaseTitle)
        assertTrue(result[2].isFavorite)
    }

    @Test
    fun `FavoritesStateFactory - empty list returns empty list`() {
        val result = favoritesStateFactory.create(emptyList())

        assertEquals(0, result.size)
        assertEquals(emptyList(), result)
    }

    // ReleaseDetailsStateFactory tests

    @Test
    fun `ReleaseDetailsStateFactory - all fields mapped correctly`() {
        val releaseDetails = createReleaseDetails(
            id = "99999",
            releaseTitle = "Details Release",
            artistTitle = "Details Artist",
            want = 150,
            have = 80,
            country = "JP",
            genres = listOf("Techno", "House", "Trance"),
            coverImage = "https://example.com/details-cover.jpg",
            isFavorite = true
        )

        val result = releaseDetailsStateFactory.create(releaseDetails)

        assertEquals("99999", result.id)
        assertEquals("Details Release", result.releaseTitle)
        assertEquals("Details Artist", result.artistTitle)
        assertEquals(150, result.want)
        assertEquals(80, result.have)
        assertEquals("JP", result.country)
        assertEquals(listOf("Techno", "House", "Trance"), result.genres)
        assertEquals("https://example.com/details-cover.jpg", result.coverImage)
        assertTrue(result.isFavorite)
    }

    @Test
    fun `ReleaseDetailsStateFactory - zero want and have values`() {
        val releaseDetails = createReleaseDetails(
            want = 0,
            have = 0
        )

        val result = releaseDetailsStateFactory.create(releaseDetails)

        assertEquals(0, result.want)
        assertEquals(0, result.have)
    }

    @Test
    fun `ReleaseDetailsStateFactory - empty genres list`() {
        val releaseDetails = createReleaseDetails(
            genres = emptyList()
        )

        val result = releaseDetailsStateFactory.create(releaseDetails)

        assertEquals(emptyList<String>(), result.genres)
    }

    @Test
    fun `ReleaseDetailsStateFactory - isFavorite false`() {
        val releaseDetails = createReleaseDetails(isFavorite = false)

        val result = releaseDetailsStateFactory.create(releaseDetails)

        assertFalse(result.isFavorite)
    }

    // Helper functions

    private fun createRelease(
        id: String = "12345",
        artistTitle: String = "Artist",
        releaseTitle: String = "Release Title",
        country: String = "US",
        genre: List<String> = listOf("Rock"),
        thumb: String = "https://example.com/thumb.jpg",
        coverImage: String = "https://example.com/cover.jpg"
    ) = Release(
        id = id,
        artistTitle = artistTitle,
        releaseTitle = releaseTitle,
        country = country,
        genre = genre,
        thumb = thumb,
        coverImage = coverImage
    )

    private fun createReleaseWithFavorite(
        release: Release = createRelease(),
        isFavorite: Boolean = false
    ) = ReleaseWithFavorite(
        release = release,
        isFavorite = isFavorite
    )

    private fun createReleaseSearch(
        id: String = "54321",
        artistTitle: String = "Search Artist",
        releaseTitle: String = "Search Title",
        country: String = "UK",
        genre: List<String> = listOf("Electronic"),
        thumb: String = "https://example.com/search-thumb.jpg"
    ) = ReleaseSearch(
        id = id,
        artistTitle = artistTitle,
        releaseTitle = releaseTitle,
        country = country,
        genre = genre,
        thumb = thumb
    )

    private fun createReleaseSearchWithFavorite(
        release: ReleaseSearch = createReleaseSearch(),
        isFavorite: Boolean = false
    ) = ReleaseSearchWithFavorite(
        release = release,
        isFavorite = isFavorite
    )

    private fun createReleaseDetails(
        id: String = "99999",
        releaseTitle: String = "Details Title",
        artistTitle: String = "Details Artist",
        want: Int = 100,
        have: Int = 50,
        country: String = "DE",
        genres: List<String> = listOf("Techno", "House"),
        coverImage: String = "https://example.com/details.jpg",
        isFavorite: Boolean = true
    ) = ReleaseDetails(
        id = id,
        releaseTitle = releaseTitle,
        artistTitle = artistTitle,
        want = want,
        have = have,
        country = country,
        genres = genres,
        coverImage = coverImage,
        isFavorite = isFavorite
    )
}
