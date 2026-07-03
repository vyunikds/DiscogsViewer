package com.example.releases

import com.example.database.dbo.CountryDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.network.dto.Community
import com.example.network.dto.ReleaseResultDto
import kotlin.test.assertEquals
import kotlinx.serialization.InternalSerializationApi
import org.junit.Before
import org.junit.Test

@OptIn(InternalSerializationApi::class)
class ReleaseDataMapperTest {
    private lateinit var mapper: ReleaseDataMapper

    @Before
    fun setup() {
        mapper = ReleaseDataMapper()
    }

    @Test
    fun `toDbo maps all fields correctly`() {
        val dto = createDto(CreateDtoConfig(title = "Artist - Title", id = 12345))

        val result = mapper.toDbo(dto)

        assertEquals("12345", result.id)
        assertEquals("Artist", result.artistTitle)
        assertEquals("Title", result.releaseTitle)
        assertEquals("https://example.com/thumb.jpg", result.thumb)
        assertEquals("https://example.com/cover.jpg", result.coverImage)
        assertEquals(5, result.communityHave)
        assertEquals(10, result.communityWant)
    }

    @Test
    fun `toDbo splits Artist - Title correctly`() {
        val dto = createDto(CreateDtoConfig(title = "Artist - Title"))

        val result = mapper.toDbo(dto)

        assertEquals("Artist", result.artistTitle)
        assertEquals("Title", result.releaseTitle)
    }

    @Test
    fun `toDbo handles multiple dashes`() {
        val dto = createDto(CreateDtoConfig(title = "Artist - Title - Remix"))

        val result = mapper.toDbo(dto)

        assertEquals("Artist", result.artistTitle)
        assertEquals("Title - Remix", result.releaseTitle)
    }

    @Test
    fun `toDbo handles title without dash`() {
        val dto = createDto(CreateDtoConfig(title = "OnlyTitle"))

        val result = mapper.toDbo(dto)

        assertEquals("OnlyTitle", result.artistTitle)
        assertEquals("OnlyTitle", result.releaseTitle)
    }

    @Test
    fun `toDbo handles leading dash`() {
        val dto = createDto(CreateDtoConfig(title = "- Title"))

        val result = mapper.toDbo(dto)

        assertEquals("", result.artistTitle)
        assertEquals("Title", result.releaseTitle)
    }

    @Test
    fun `toDbo handles trailing dash`() {
        val dto = createDto(CreateDtoConfig(title = "Artist -"))

        val result = mapper.toDbo(dto)

        assertEquals("Artist", result.artistTitle)
        assertEquals("", result.releaseTitle)
    }

    @Test
    fun `toDbo handles empty title`() {
        val dto = createDto(CreateDtoConfig(title = ""))

        val result = mapper.toDbo(dto)

        assertEquals("", result.artistTitle)
        assertEquals("", result.releaseTitle)
    }

    @Test
    fun `toReleaseGenres with single genre`() {
        val dto = createDto(CreateDtoConfig(genre = listOf("Rock")))

        val result = mapper.toReleaseGenres(dto)

        assertEquals(1, result.size)
        assertEquals(ReleaseGenreDbo("12345", "Rock"), result[0])
    }

    @Test
    fun `toReleaseGenres with multiple genres`() {
        val dto = createDto(CreateDtoConfig(genre = listOf("Rock", "Pop", "Jazz")))

        val result = mapper.toReleaseGenres(dto)

        assertEquals(3, result.size)
        assertEquals("12345", result[0].releaseId)
        assertEquals("Rock", result[0].genre)
        assertEquals("12345", result[1].releaseId)
        assertEquals("Pop", result[1].genre)
        assertEquals("12345", result[2].releaseId)
        assertEquals("Jazz", result[2].genre)
    }

    @Test
    fun `toReleaseGenres with empty genre list`() {
        val dto = createDto(CreateDtoConfig(genre = emptyList()))

        val result = mapper.toReleaseGenres(dto)

        assertEquals(emptyList<ReleaseGenreDbo>(), result)
    }

    @Test
    fun `toReleaseCountries returns single item with correct ID and country`() {
        val dto = createDto(CreateDtoConfig(id = 99999, country = "US"))

        val result = mapper.toReleaseCountries(dto)

        assertEquals(1, result.size)
        assertEquals(ReleaseCountryDbo("99999", "US"), result[0])
    }

    @Test
    fun `toDbos maps list of DTOs to list of DBOs`() {
        val dtos =
            listOf(
                createDto(CreateDtoConfig(id = 1, title = "A1 - T1")),
                createDto(CreateDtoConfig(id = 2, title = "A2 - T2")),
                createDto(CreateDtoConfig(id = 3, title = "A3 - T3")),
            )

        val result = mapper.toDbos(dtos)

        assertEquals(3, result.size)
        assertEquals("1", result[0].id)
        assertEquals("A1", result[0].artistTitle)
        assertEquals("T1", result[0].releaseTitle)
        assertEquals("2", result[1].id)
        assertEquals("A2", result[1].artistTitle)
        assertEquals("T2", result[1].releaseTitle)
        assertEquals("3", result[2].id)
        assertEquals("A3", result[2].artistTitle)
        assertEquals("T3", result[2].releaseTitle)
    }

    @Test
    fun `toReleaseGenresBatch flatMaps multiple DTOs genres`() {
        val dtos =
            listOf(
                createDto(CreateDtoConfig(id = 1, genre = listOf("Rock", "Pop"))),
                createDto(CreateDtoConfig(id = 2, genre = listOf("Jazz"))),
            )

        val result = mapper.toReleaseGenresBatch(dtos)

        assertEquals(3, result.size)
        assertEquals(ReleaseGenreDbo("1", "Rock"), result[0])
        assertEquals(ReleaseGenreDbo("1", "Pop"), result[1])
        assertEquals(ReleaseGenreDbo("2", "Jazz"), result[2])
    }

    @Test
    fun `toReleaseCountriesBatch flatMaps multiple DTOs countries`() {
        val dtos =
            listOf(
                createDto(CreateDtoConfig(id = 1, country = "US")),
                createDto(CreateDtoConfig(id = 2, country = "UK")),
                createDto(CreateDtoConfig(id = 3, country = "DE")),
            )

        val result = mapper.toReleaseCountriesBatch(dtos)

        assertEquals(3, result.size)
        assertEquals(ReleaseCountryDbo("1", "US"), result[0])
        assertEquals(ReleaseCountryDbo("2", "UK"), result[1])
        assertEquals(ReleaseCountryDbo("3", "DE"), result[2])
    }

    @Test
    fun `toGenres deduplicates by genre name`() {
        val releaseGenres =
            listOf(
                ReleaseGenreDbo("1", "Rock"),
                ReleaseGenreDbo("2", "Rock"),
                ReleaseGenreDbo("3", "Pop"),
                ReleaseGenreDbo("4", "Jazz"),
                ReleaseGenreDbo("5", "Pop"),
            )

        val result = mapper.toGenres(releaseGenres)

        assertEquals(3, result.size)
        assertEquals(GenreDbo("Rock"), result[0])
        assertEquals(GenreDbo("Pop"), result[1])
        assertEquals(GenreDbo("Jazz"), result[2])
    }

    @Test
    fun `toCountries deduplicates by country name`() {
        val releaseCountries =
            listOf(
                ReleaseCountryDbo("1", "US"),
                ReleaseCountryDbo("2", "US"),
                ReleaseCountryDbo("3", "UK"),
                ReleaseCountryDbo("4", "DE"),
                ReleaseCountryDbo("5", "UK"),
            )

        val result = mapper.toCountries(releaseCountries)

        assertEquals(3, result.size)
        assertEquals(CountryDbo("US"), result[0])
        assertEquals(CountryDbo("UK"), result[1])
        assertEquals(CountryDbo("DE"), result[2])
    }

    private data class CreateDtoConfig(
        val title: String = "Artist - Title",
        val country: String = "US",
        val id: Int = 12345,
        val genre: List<String> = listOf("Rock"),
        val thumb: String = "https://example.com/thumb.jpg",
        val coverImage: String = "https://example.com/cover.jpg",
        val community: Community = Community(want = 10, have = 5),
    )

    private fun createDto(config: CreateDtoConfig = CreateDtoConfig()): ReleaseResultDto =
        ReleaseResultDto(
            title = config.title,
            country = config.country,
            id = config.id,
            genre = config.genre,
            thumb = config.thumb,
            coverImage = config.coverImage,
            community = config.community,
        )
}
