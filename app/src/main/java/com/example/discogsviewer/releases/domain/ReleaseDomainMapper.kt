package com.example.discogsviewer.releases.domain

import com.example.database.dbo.TopReleasesDbo
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleaseDomainMapper @Inject constructor() {
    @OptIn(InternalSerializationApi::class)
    fun fromEntity(releaseDbo: TopReleasesDbo): Release {
        return Release(
            artistTitle = releaseDbo.artistTitle,
            releaseTitle = releaseDbo.releaseTitle,
            country = releaseDbo.country,
            genre = releaseDbo.genres,
            id = releaseDbo.id,
            thumb = releaseDbo.thumb,
        )
    }
}
