package com.example.releases

import com.example.database.dbo.CountryDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.network.dto.ReleaseResultDto
import javax.inject.Inject
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class ReleaseDataMapper
    @Inject
    constructor() {
        fun toDbo(releaseDto: ReleaseResultDto): ReleaseDbo =
            ReleaseDbo(
                id = releaseDto.id.toString(),
                artistTitle = extractArtistFromTitle(releaseDto.title),
                releaseTitle = extractReleaseFromTitle(releaseDto.title),
                thumb = releaseDto.thumb,
                coverImage = releaseDto.coverImage,
                communityHave = releaseDto.community.have,
                communityWant = releaseDto.community.want,
            )

        fun toReleaseGenres(releaseDto: ReleaseResultDto): List<ReleaseGenreDbo> {
            val releaseId = releaseDto.id.toString()
            return releaseDto.genre.map {
                ReleaseGenreDbo(releaseId, it)
            }
        }

        fun toReleaseCountries(releaseDto: ReleaseResultDto): List<ReleaseCountryDbo> {
            val releaseId = releaseDto.id.toString()
            return listOf(ReleaseCountryDbo(releaseId, releaseDto.country))
        }

        fun toDbos(releaseDtos: List<ReleaseResultDto>): List<ReleaseDbo> = releaseDtos.map { toDbo(it) }

        fun toReleaseGenresBatch(releaseDtos: List<ReleaseResultDto>): List<ReleaseGenreDbo> =
            releaseDtos.flatMap {
                toReleaseGenres(it)
            }

        fun toReleaseCountriesBatch(releaseDtos: List<ReleaseResultDto>): List<ReleaseCountryDbo> =
            releaseDtos.flatMap(::toReleaseCountries)

        fun toGenres(releaseGenres: List<ReleaseGenreDbo>): List<GenreDbo> =
            releaseGenres
                .map {
                    GenreDbo(it.genre)
                }.distinctBy { it.genre }

        fun toCountries(releaseCountries: List<ReleaseCountryDbo>): List<CountryDbo> =
            releaseCountries
                .map {
                    CountryDbo(it.country)
                }.distinctBy { it.country }
    }

private fun extractArtistFromTitle(title: String): String = title.substringBefore("-").trimEnd()

private fun extractReleaseFromTitle(title: String): String = title.substringAfter("-").trimStart()
