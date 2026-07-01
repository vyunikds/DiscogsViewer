package com.example.feature.releases.state

import com.example.feature.releases.domain.ReleaseWithFavorite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleasesStateFactory @Inject constructor() {
    fun create(release: ReleaseWithFavorite): ReleaseState {
        return ReleaseState(
            id = release.release.id,
            releaseTitle = release.release.releaseTitle,
            country = release.release.country,
            thumb = release.release.thumb,
            artistTitle = release.release.artistTitle,
            genre = release.release.genre,
            coverImage = release.release.coverImage,
            isFavorite = release.isFavorite,
        )
    }
}
