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
//            genre = TODO(),
//            format = TODO(),
//            style = TODO(),
//            label = TODO(),
//            type = TODO(),
//            barcode = TODO(),
//            masterId = TODO(),
//            masterUrl = TODO(),
//            uri = TODO(),
//            catno = TODO(),
//            coverImage = TODO(),
//            resourceUrl = TODO(),
//            formatQuantity = TODO(),
//            inCollection = TODO(),
//            inWantlist = TODO(),
//            communityHave = TODO(),
//            communityWant = TODO()
        )
    }
}

private fun extractArtistFromTitle(title: String): String {
    return title.substringBefore("-").trimEnd()
}

private fun extractReleaseFromTitle(title: String): String {
    return title.substringAfter("-").trimStart()
}