package com.example.discogsviewer.search.domain

import com.example.network.dto.Community
import com.example.network.dto.ReleaseResultDto
import kotlinx.serialization.InternalSerializationApi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(InternalSerializationApi::class)
class ReleaseSearchDomainMapperTest {

    private lateinit var mapper: ReleaseSearchDomainMapper

    @Before
    fun setup() {
        mapper = ReleaseSearchDomainMapper()
    }

    @Test
    fun `normal case - artist dash title parses correctly`() {
        val dto = createDto(title = "Artist - Title")

        val result = mapper.fromEntity(dto)

        assertEquals("Artist", result.artistTitle)
        assertEquals("Title", result.releaseTitle)
    }

    @Test
    fun `no dash - substring fallback returns full string for both`() {
        val dto = createDto(title = "Artstonly")

        val result = mapper.fromEntity(dto)

        assertEquals("Artstonly", result.artistTitle)
        assertEquals("Artstonly", result.releaseTitle)
    }

    @Test
    fun `multiple dashes - artist is first part and release includes remaining dashes`() {
        val dto = createDto(title = "Artist - Title - Remix")

        val result = mapper.fromEntity(dto)

        assertEquals("Artist", result.artistTitle)
        assertEquals("Title - Remix", result.releaseTitle)
    }

    @Test
    fun `leading dash - artist empty and release is trimmed title`() {
        val dto = createDto(title = "- Title")

        val result = mapper.fromEntity(dto)

        assertEquals("", result.artistTitle)
        assertEquals("Title", result.releaseTitle)
    }

    @Test
    fun `trailing dash - artist is artist and release empty`() {
        val dto = createDto(title = "Artist -")

        val result = mapper.fromEntity(dto)

        assertEquals("Artist", result.artistTitle)
        assertEquals("", result.releaseTitle)
    }

    @Test
    fun `empty string - both artist and release are empty`() {
        val dto = createDto(title = "")

        val result = mapper.fromEntity(dto)

        assertEquals("", result.artistTitle)
        assertEquals("", result.releaseTitle)
    }

    @Test
    fun `just dash - both artist and release are empty`() {
        val dto = createDto(title = "-")

        val result = mapper.fromEntity(dto)

        assertEquals("", result.artistTitle)
        assertEquals("", result.releaseTitle)
    }

    @Test
    fun `extra spaces - trims trailing artist spaces and leading release spaces`() {
        val dto = createDto(title = "  Artist  -  Title  ")

        val result = mapper.fromEntity(dto)

        assertEquals("  Artist", result.artistTitle)
        assertEquals("Title  ", result.releaseTitle)
    }

    @Test
    fun `all fields map from DTO correctly`() {
        val dto = createDto(
            title = "Artist - Title",
            country = "US",
            id = 12345,
            genre = listOf("Rock", "Pop"),
            thumb = "https://example.com/thumb.jpg"
        )

        val result = mapper.fromEntity(dto)

        assertEquals("12345", result.id)
        assertEquals("Artist", result.artistTitle)
        assertEquals("Title", result.releaseTitle)
        assertEquals("US", result.country)
        assertEquals(listOf("Rock", "Pop"), result.genre)
        assertEquals("https://example.com/thumb.jpg", result.thumb)
    }

    @Test
    fun `genre list mapped correctly`() {
        val genre = listOf("Electronic", "Ambient", "Downtemp")
        val dto = createDto(genre = genre)

        val result = mapper.fromEntity(dto)

        assertEquals(genre, result.genre)
    }

    @Test
    fun `empty genre list mapped correctly`() {
        val dto = createDto(genre = emptyList())

        val result = mapper.fromEntity(dto)

        assertEquals(emptyList(), result.genre)
    }

    private fun createDto(
        title: String = "Artist - Title",
        country: String = "US",
        id: Int = 12345,
        genre: List<String> = listOf("Rock"),
        thumb: String = "https://example.com/thumb.jpg"
    ): ReleaseResultDto {
        return ReleaseResultDto(
            title = title,
            country = country,
            id = id,
            genre = genre,
            thumb = thumb,
            coverImage = "https://example.com/cover.jpg",
            community = Community(want = 10, have = 5)
        )
    }
}
