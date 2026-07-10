package com.example.discogsviewer

import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.database.dbo.TopReleaseDbo

object TestFixtures {
    fun createRelease(
        id: String = "1",
        artistTitle: String = "Pink Floyd",
        releaseTitle: String = "The Dark Side of the Moon",
        thumb: String = "",
        coverImage: String = "",
        communityHave: Int = 5000,
        communityWant: Int = 3000,
    ) = ReleaseDbo(
        id = id,
        artistTitle = artistTitle,
        releaseTitle = releaseTitle,
        thumb = thumb,
        coverImage = coverImage,
        communityHave = communityHave,
        communityWant = communityWant,
    )

    fun createGenre(genre: String) = GenreDbo(genre = genre)

    fun createCountry(country: String) = CountryDbo(country = country)

    fun createReleaseGenre(releaseId: String, genre: String) = ReleaseGenreDbo(
        releaseId = releaseId,
        genre = genre,
    )

    fun createReleaseCountry(releaseId: String, country: String) = ReleaseCountryDbo(
        releaseId = releaseId,
        country = country,
    )

    fun createTopRelease(releaseId: String) = TopReleaseDbo(releaseId = releaseId)

    fun createFavorite(releaseId: String, addedAt: Long = System.currentTimeMillis()) = FavoriteDbo(
        releaseId = releaseId,
        addedAt = addedAt,
    )

    class TestReleaseData(
        val id: String = "1",
        val artistTitle: String = "Pink Floyd",
        val releaseTitle: String = "The Dark Side of the Moon",
        val thumb: String = "",
        val coverImage: String = "",
        val genres: List<String> = listOf("Progressive Rock", "Psychedelic Rock"),
        val country: String = "UK",
        val communityHave: Int = 5000,
        val communityWant: Int = 3000,
        val favorite: Boolean = false,
    ) {
        fun toReleaseDbo(): ReleaseDbo = createRelease(
            id = id,
            artistTitle = artistTitle,
            releaseTitle = releaseTitle,
            thumb = thumb,
            coverImage = coverImage,
            communityHave = communityHave,
            communityWant = communityWant,
        )

        fun toGenres(): List<GenreDbo> = genres.map(::createGenre)

        fun toCountries(): List<CountryDbo> = listOf(createCountry(country))

        fun toReleaseGenres(): List<ReleaseGenreDbo> = genres.map { createReleaseGenre(id, it) }

        fun toReleaseCountries(): List<ReleaseCountryDbo> = listOf(createReleaseCountry(id, country))

        fun toTopRelease(): TopReleaseDbo = createTopRelease(id)

        fun toFavorite(): FavoriteDbo = createFavorite(id)
    }

    fun threeReleasesTestData(): List<TestReleaseData> = listOf(
        TestReleaseData(
            id = "1",
            artistTitle = "Pink Floyd",
            releaseTitle = "The Dark Side of the Moon",
            genres = listOf("Progressive Rock"),
            country = "UK",
        ),
        TestReleaseData(
            id = "2",
            artistTitle = "Daft Punk",
            releaseTitle = "Random Access Memories",
            genres = listOf("Electronic", "House"),
            country = "France",
        ),
        TestReleaseData(
            id = "3",
            artistTitle = "Metallica",
            releaseTitle = "Master of Puppets",
            genres = listOf("Metal", "Thrash Metal"),
            country = "USA",
        ),
    )
}
