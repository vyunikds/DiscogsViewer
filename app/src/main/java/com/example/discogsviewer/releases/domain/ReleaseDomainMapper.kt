package com.example.discogsviewer.releases.domain

import com.example.database.dbo.FullReleaseDbo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleaseDomainMapper @Inject constructor() {
    fun fromEntity(fullRelease: FullReleaseDbo): Release {
        return Release(
            id = fullRelease.release.id,
            artistTitle = fullRelease.release.artistTitle,
            releaseTitle = fullRelease.release.releaseTitle,
            country = fullRelease.countriesList.firstOrNull()?.country ?: "",
            genre = fullRelease.genresList.map { it.genre },
            thumb = fullRelease.release.thumb,
            coverImage = fullRelease.release.coverImage,
        )
    }
}
