package com.example.releases

import com.example.database.dbo.TopReleasesDbo
import com.example.network.dto.ReleaseResultDto
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

class ReleaseDataMapper @Inject constructor() {
    @OptIn(InternalSerializationApi::class)
    fun toDbo(releaseDto: ReleaseResultDto): TopReleasesDbo {
        return TopReleasesDbo(
            id = releaseDto.id,
            artistTitle = extractArtistFromTitle(releaseDto.title),
            releaseTitle = extractReleaseFromTitle(releaseDto.title),
            country = releaseDto.country,
            thumb = releaseDto.thumb,
            coverImage = releaseDto.coverImage,
            genres = releaseDto.genre,
            communityHave = releaseDto.community.have,
            communityWant = releaseDto.community.want,
        )
    }
}

private fun extractArtistFromTitle(title: String): String {
    return title.substringBefore("-").trimEnd()
}

private fun extractReleaseFromTitle(title: String): String {
    return title.substringAfter("-").trimStart()
}