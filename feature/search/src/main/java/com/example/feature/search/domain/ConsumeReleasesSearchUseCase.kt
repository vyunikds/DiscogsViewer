package com.example.feature.search.domain

import com.example.favorite.FavoritesRepository
import com.example.search.ReleaseSearchRepository
import com.example.search.ReleaseSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

data class SearchResultPage(
    val releases: List<ReleaseSearchWithFavorite>,
    val hasNextPage: Boolean,
)

@Singleton
class ConsumeReleasesSearchUseCase @Inject constructor(
    private val releasesSearchRepository: ReleaseSearchRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(title: String, page: Int): Flow<SearchResultPage> {
        return combine(
            releasesSearchRepository.consumeReleaseSearch(title, page),
            favoritesRepository.consumeReleaseIds()
        ) { results: List<ReleaseSearchResult>, releaseIds ->
            val hasNextPage = results.lastOrNull()?.hasNextPage ?: false
            val releases = results.map { release: ReleaseSearchResult ->
                ReleaseSearchWithFavorite(
                    release = ReleaseSearch(
                        id = release.id,
                        artistTitle = release.artistTitle,
                        releaseTitle = release.releaseTitle,
                        country = release.country,
                        genre = release.genre,
                        thumb = release.thumb,
                    ),
                    isFavorite = release.id in releaseIds
                )
            }
            SearchResultPage(
                releases = releases,
                hasNextPage = hasNextPage,
            )
        }
    }
}
