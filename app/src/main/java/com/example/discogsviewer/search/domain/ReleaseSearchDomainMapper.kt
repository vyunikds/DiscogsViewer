package com.example.discogsviewer.search.domain

import com.example.network.dto.ReleaseResultDto
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(InternalSerializationApi::class)
@Singleton
class ReleaseSearchDomainMapper @Inject constructor() {
    fun fromEntity(releaseResultDto: ReleaseResultDto): ReleaseSearch {
        return ReleaseSearch(
            artistTitle = extractArtistFromTitle(releaseResultDto.title),
            releaseTitle = extractReleaseFromTitle(releaseResultDto.title),
            country = releaseResultDto.country,
            genre = releaseResultDto.genre,
            id = releaseResultDto.id.toString(),
            thumb = releaseResultDto.thumb,
        )
    }
}

private fun extractArtistFromTitle(title: String): String {
    return title.substringBefore("-").trimEnd()
}

private fun extractReleaseFromTitle(title: String): String {
    return title.substringAfter("-").trimStart()
}
