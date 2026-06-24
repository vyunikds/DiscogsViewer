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
                    id = item.releaseId.toIntOrNull() ?: 0,
                    artistTitle = item.artistTitle,
                    releaseTitle = item.releaseTitle,
                    country = item.country,
                    genre = item.genres,
                    thumb = item.thumb,
                    coverImage = item.coverImage ?: "",
                ),
                isFavorite = true,
            )
        }
    }
}
