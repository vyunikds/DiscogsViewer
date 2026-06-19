package com.example.discogsviewer.details.domain

import com.example.database.dbo.TopReleasesDbo
import com.example.network.dto.ReleaseDetailsDto
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

class ReleaseDetailsDomainMapper @Inject constructor() {
    @OptIn(InternalSerializationApi::class)
    fun fromEntity(releaseDbo: TopReleasesDbo): ReleaseDetails {
        return ReleaseDetails(
            releaseTitle = releaseDbo.releaseTitle,
            country = releaseDbo.country,
            genres = releaseDbo.genres,
            id = releaseDbo.id,
            coverImage = releaseDbo.coverImage,
            isFavorite = false,
            artistTitle = releaseDbo.artistTitle,
            want = releaseDbo.communityHave,
            have = releaseDbo.communityWant,
        )
    }

    @OptIn(InternalSerializationApi::class)
    fun fromDto(releaseDto: ReleaseDetailsDto): ReleaseDetails {
        val artistName = releaseDto.artistsNames.firstOrNull { it.isNotBlank() }
            ?: releaseDto.artists.firstOrNull { it.name.isNotBlank() }?.name
            ?: ""
        val community = releaseDto.community
        val coverImage = releaseDto.images.firstOrNull { it.type == "primary" }?.uri
            ?: releaseDto.images.firstOrNull()?.uri
            ?: releaseDto.thumb
            ?: releaseDto.coverImage
        return ReleaseDetails(
            id = releaseDto.id,
            releaseTitle = releaseDto.title,
            artistTitle = artistName,
            country = releaseDto.country,
            genres = releaseDto.genres,
            coverImage = coverImage,
            isFavorite = false,
            want = community?.want ?: 0,
            have = community?.have ?: 0,
        )
    }
}
