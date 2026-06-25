package com.example.discogsviewer.releases.domain

import com.example.database.dbo.CountryDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.FullReleaseDbo
import com.example.database.dbo.ReleaseDbo
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ReleaseDomainMapperTest {

    private lateinit var mapper: ReleaseDomainMapper

    @Before
    fun setup() {
        mapper = ReleaseDomainMapper()
    }

    @Test
    fun `normal case - all fields mapped correctly`() {
        val dbo = createDbo(
            release = createReleaseDbo(
                id = "12345",
                artistTitle = "Artist Name",
                releaseTitle = "Release Title",
                thumb = "https://example.com/thumb.jpg",
                coverImage = "https://example.com/cover.jpg"
            ),
            genres = listOf(createGenreDbo("Rock"), createGenreDbo("Pop")),
            countries = listOf(createCountryDbo("US"))
        )

        val result = mapper.fromEntity(dbo)

        assertEquals("12345", result.id)
        assertEquals("Artist Name", result.artistTitle)
        assertEquals("Release Title", result.releaseTitle)
        assertEquals("US", result.country)
        assertEquals(listOf("Rock", "Pop"), result.genre)
        assertEquals("https://example.com/thumb.jpg", result.thumb)
        assertEquals("https://example.com/cover.jpg", result.coverImage)
    }

    @Test
    fun `empty countriesList - country defaults to empty string`() {
        val dbo = createDbo(
            countries = emptyList()
        )

        val result = mapper.fromEntity(dbo)

        assertEquals("", result.country)
    }

    @Test
    fun `multiple countries - only first country is used`() {
        val dbo = createDbo(
            countries = listOf(
                createCountryDbo("US"),
                createCountryDbo("UK"),
                createCountryDbo("DE")
            )
        )

        val result = mapper.fromEntity(dbo)

        assertEquals("US", result.country)
    }

    @Test
    fun `empty genresList - genre is empty list`() {
        val dbo = createDbo(
            genres = emptyList()
        )

        val result = mapper.fromEntity(dbo)

        assertEquals(emptyList<String>(), result.genre)
    }

    @Test
    fun `multiple genres - all genres mapped`() {
        val dbo = createDbo(
            genres = listOf(
                createGenreDbo("Electronic"),
                createGenreDbo("Ambient"),
                createGenreDbo("Downtempo")
            )
        )

        val result = mapper.fromEntity(dbo)

        assertEquals(listOf("Electronic", "Ambient", "Downtempo"), result.genre)
    }

    @Test
    fun `all nested ReleaseDbo fields mapped correctly`() {
        val dbo = createDbo(
            release = createReleaseDbo(
                id = "99999",
                artistTitle = "Nested Artist",
                releaseTitle = "Nested Title",
                thumb = "https://example.com/nested-thumb.jpg",
                coverImage = "https://example.com/nested-cover.jpg"
            )
        )

        val result = mapper.fromEntity(dbo)

        assertEquals("99999", result.id)
        assertEquals("Nested Artist", result.artistTitle)
        assertEquals("Nested Title", result.releaseTitle)
        assertEquals("https://example.com/nested-thumb.jpg", result.thumb)
        assertEquals("https://example.com/nested-cover.jpg", result.coverImage)
    }

    private fun createDbo(
        release: ReleaseDbo = createReleaseDbo(),
        genres: List<GenreDbo> = listOf(createGenreDbo("Rock")),
        countries: List<CountryDbo> = listOf(createCountryDbo("US"))
    ): FullReleaseDbo {
        return FullReleaseDbo(
            release = release,
            genresList = genres,
            countriesList = countries
        )
    }

    private fun createReleaseDbo(
        id: String = "12345",
        artistTitle: String = "Artist",
        releaseTitle: String = "Title",
        thumb: String = "https://example.com/thumb.jpg",
        coverImage: String = "https://example.com/cover.jpg"
    ): ReleaseDbo {
        return ReleaseDbo(
            id = id,
            artistTitle = artistTitle,
            releaseTitle = releaseTitle,
            thumb = thumb,
            coverImage = coverImage
        )
    }

    private fun createGenreDbo(genre: String = "Rock"): GenreDbo {
        return GenreDbo(genre = genre)
    }

    private fun createCountryDbo(country: String = "US"): CountryDbo {
        return CountryDbo(country = country)
    }
}
