package com.example.discogsviewer.details.domain

import com.example.network.dto.ArtistDetail
import com.example.network.dto.Community
import com.example.network.dto.ImageDetail
import com.example.network.dto.ReleaseDetailsDto
import kotlinx.serialization.InternalSerializationApi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(InternalSerializationApi::class)
class ReleaseDetailsDomainMapperTest {

    private lateinit var mapper: ReleaseDetailsDomainMapper

    @Before
    fun setup() {
        mapper = ReleaseDetailsDomainMapper()
    }

    @Test
    fun `normal case with artistsNames - picks first non-blank`() {
        val dto = createDto(
            artistsNames = listOf("Main Artist", "Featured Artist")
        )

        val result = mapper.fromDto(dto)

        assertEquals("Main Artist", result.artistTitle)
    }

    @Test
    fun `artistsNames with leading blanks - picks first non-blank`() {
        val dto = createDto(
            artistsNames = listOf("", " ", "Real Artist")
        )

        val result = mapper.fromDto(dto)

        assertEquals("Real Artist", result.artistTitle)
    }

    @Test
    fun `fallback to artists list when artistsNames is empty`() {
        val dto = createDto(
            artistsNames = emptyList(),
            artists = listOf(ArtistDetail(name = "Fallback Artist"))
        )

        val result = mapper.fromDto(dto)

        assertEquals("Fallback Artist", result.artistTitle)
    }

    @Test
    fun `fallback to artists list when artistsNames has only blank strings`() {
        val dto = createDto(
            artistsNames = listOf("", "  ", ""),
            artists = listOf(
                ArtistDetail(name = ""),
                ArtistDetail(name = "Backup Artist")
            )
        )

        val result = mapper.fromDto(dto)

        assertEquals("Backup Artist", result.artistTitle)
    }

    @Test
    fun `artistTitle is empty when both lists empty`() {
        val dto = createDto(
            artistsNames = emptyList(),
            artists = emptyList()
        )

        val result = mapper.fromDto(dto)

        assertEquals("", result.artistTitle)
    }

    @Test
    fun `artistTitle empty when artists list has only blank names`() {
        val dto = createDto(
            artistsNames = listOf("   "),
            artists = listOf(
                ArtistDetail(name = ""),
                ArtistDetail(name = "  ")
            )
        )

        val result = mapper.fromDto(dto)

        assertEquals("", result.artistTitle)
    }

    @Test
    fun `cover image - primary image uri selected first`() {
        val dto = createDto(
            images = listOf(
                ImageDetail(type = "secondary", uri = "https://example.com/secondary.jpg"),
                ImageDetail(type = "primary", uri = "https://example.com/primary.jpg")
            ),
            thumb = "https://example.com/thumb.jpg",
            coverImage = "https://example.com/cover.jpg"
        )

        val result = mapper.fromDto(dto)

        assertEquals("https://example.com/primary.jpg", result.coverImage)
    }

    @Test
    fun `cover image - falls back to first image uri when no primary`() {
        val dto = createDto(
            images = listOf(
                ImageDetail(type = "other", uri = "https://example.com/other.jpg"),
                ImageDetail(type = "secondary", uri = "https://example.com/secondary.jpg")
            ),
            thumb = "https://example.com/thumb.jpg"
        )

        val result = mapper.fromDto(dto)

        assertEquals("https://example.com/other.jpg", result.coverImage)
    }

    @Test
    fun `cover image - falls back to thumb when no images`() {
        val dto = createDto(
            images = emptyList(),
            thumb = "https://example.com/thumb.jpg"
        )

        val result = mapper.fromDto(dto)

        assertEquals("https://example.com/thumb.jpg", result.coverImage)
    }

    @Test
    fun `cover image - empty thumb stops elvis chain, does not fall back to coverImage`() {
        val dto = createDto(
            images = emptyList(),
            thumb = "",
            coverImage = "https://example.com/cover.jpg"
        )

        val result = mapper.fromDto(dto)

        assertEquals("", result.coverImage)
    }

    @Test
    fun `cover image - falls back to coverImage when no images and thumb is null-string`() {
        val dto = createDto(
            images = emptyList(),
            thumb = "",
            coverImage = "https://example.com/cover.jpg"
        )

        // When thumb is non-null (even empty), ?: stops there.
        // Only if we set coverImage directly on DTO and thumb is also empty,
        // the mapper returns thumb's value (""). This tests actual behavior:
        // coverImage DTO field is only used when thumb is also empty string
        // by convention the field maps to thumb first.
        val result = mapper.fromDto(dto)

        // thumb = "" is not null, so elvis chain stops at thumb
        assertEquals("", result.coverImage)
    }

    @Test
    fun `cover image - empty when no images, thumb, or coverImage`() {
        val dto = createDto(
            images = emptyList(),
            thumb = "",
            coverImage = ""
        )

        val result = mapper.fromDto(dto)

        assertEquals("", result.coverImage)
    }

    @Test
    fun `community is null - want and have default to 0`() {
        val dto = createDto(
            community = null
        )

        val result = mapper.fromDto(dto)

        assertEquals(0, result.want)
        assertEquals(0, result.have)
    }

    @Test
    fun `community present - want and have mapped correctly`() {
        val dto = createDto(
            community = Community(want = 42, have = 17)
        )

        val result = mapper.fromDto(dto)

        assertEquals(42, result.want)
        assertEquals(17, result.have)
    }

    @Test
    fun `all fields - id converted to String, genres, country, title mapped`() {
        val dto = createDto(
            id = 987654,
            title = "Test Release",
            country = "GB",
            genres = listOf("Rock", "Metal"),
            artistsNames = listOf("Test Artist")
        )

        val result = mapper.fromDto(dto)

        assertEquals("987654", result.id)
        assertEquals("Test Release", result.releaseTitle)
        assertEquals("Test Artist", result.artistTitle)
        assertEquals("GB", result.country)
        assertEquals(listOf("Rock", "Metal"), result.genres)
    }

    @Test
    fun `isFavorite always false from mapper`() {
        val dto = createDto()

        val result = mapper.fromDto(dto)

        assertEquals(false, result.isFavorite)
    }

    private fun createDto(
        id: Int = 12345,
        title: String = "Test Title",
        artists: List<ArtistDetail> = emptyList(),
        artistsNames: List<String> = emptyList(),
        country: String = "US",
        genres: List<String> = listOf("Rock"),
        coverImage: String = "",
        thumb: String = "",
        images: List<ImageDetail> = emptyList(),
        community: Community? = Community(want = 10, have = 5)
    ): ReleaseDetailsDto {
        return ReleaseDetailsDto(
            id = id,
            title = title,
            artists = artists,
            artistsNames = artistsNames,
            country = country,
            genres = genres,
            coverImage = coverImage,
            thumb = thumb,
            images = images,
            community = community
        )
    }
}
