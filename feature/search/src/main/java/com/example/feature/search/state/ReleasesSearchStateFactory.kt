package com.example.feature.search.state

import com.example.feature.search.domain.ReleaseSearchWithFavorite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleasesSearchStateFactory @Inject constructor() {
    fun create(release: ReleaseSearchWithFavorite): ReleaseSearchState {
        return ReleaseSearchState(
            id = release.release.id,
            releaseTitle = release.release.releaseTitle,
            country = release.release.country,
            thumb = release.release.thumb,
            artistTitle = release.release.artistTitle,
            genre = release.release.genre,
            isFavorite = release.isFavorite,
        )
    }
}
