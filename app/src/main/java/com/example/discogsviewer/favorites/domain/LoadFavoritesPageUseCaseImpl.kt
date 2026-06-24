package com.example.discogsviewer.favorites.domain

import com.example.discogsviewer.releases.domain.Release
import com.example.discogsviewer.releases.domain.ReleaseWithFavorite
import com.example.favorite.FavoritesRepository
import javax.inject.Inject

class LoadFavoritesPageUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : LoadFavoritesPageUseCase {

    override suspend operator fun invoke(sortMode: FavoriteSortMode, limit: Int, offset: Int): List<ReleaseWithFavorite> {
        val items = favoritesRepository.consumePaginated(sortMode.toDataSourceSortMode(), limit, offset)
        return items.map { item ->
            val full = item.fullRelease
            ReleaseWithFavorite(
                release = Release(
                    id = full.release.id,
                    artistTitle = full.release.artistTitle,
                    releaseTitle = full.release.releaseTitle,
                    country = full.countriesList.firstOrNull()?.country ?: "",
                    genre = full.genresList.map { g -> g.genre },
                    thumb = full.release.thumb,
                    coverImage = full.release.coverImage,
                ),
                isFavorite = true,
            )
        }
    }
}
