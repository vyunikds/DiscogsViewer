package com.example.discogsviewer.favorites.domain

import com.example.discogsviewer.releases.domain.Release
import com.example.discogsviewer.releases.domain.ReleaseWithFavorite
import com.example.favorite.FavoritesRepository
import javax.inject.Inject

class LoadFavoritesPageUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : LoadFavoritesPageUseCase {

    override suspend operator fun invoke(
        sortMode: FavoriteSortMode,
        limit: Int,
        offset: Int,
        genre: String?,
    ): List<ReleaseWithFavorite> {
        val dsSortMode = sortMode.toDataSourceSortMode()
        val items = favoritesRepository.consumePaginated(dsSortMode, limit, offset, genre)
        return items.map { item ->
            ReleaseWithFavorite(
                release = Release(
                    id = item.releaseId,
                    artistTitle = item.fullRelease.release.artistTitle,
                    releaseTitle = item.fullRelease.release.releaseTitle,
                    country = item.fullRelease.countriesList.firstOrNull()?.country ?: "",
                    genre = item.fullRelease.genresList.map { it.genre },
                    thumb = item.fullRelease.release.thumb,
                    coverImage = item.fullRelease.release.coverImage,
                ),
                isFavorite = true,
            )
        }
    }
}
