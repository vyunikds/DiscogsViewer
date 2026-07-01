package com.example.feature.favorites.state

import com.example.feature.favorites.domain.ReleaseWithFavorite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesStateFactory @Inject constructor() {
    fun create(favorites: List<ReleaseWithFavorite>): List<FavoriteReleasesState> {
        return favorites.map { createFavoriteState(it) }
    }

    private fun createFavoriteState(releaseWithFavorite: ReleaseWithFavorite): FavoriteReleasesState {
        return FavoriteReleasesState(
            id = releaseWithFavorite.release.id,
            artistTitle = releaseWithFavorite.release.artistTitle,
            releaseTitle = releaseWithFavorite.release.releaseTitle,
            country = releaseWithFavorite.release.country,
            genre = releaseWithFavorite.release.genre,
            thumb = releaseWithFavorite.release.thumb,
            coverImage = releaseWithFavorite.release.coverImage,
            isFavorite = releaseWithFavorite.isFavorite,
        )
    }
}
