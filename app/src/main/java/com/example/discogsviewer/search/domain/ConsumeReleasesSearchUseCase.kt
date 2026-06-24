package com.example.discogsviewer.search.domain

import com.example.favorite.FavoritesRepository
import com.example.search.ReleaseSearchRepository
import com.example.network.dto.ReleaseResultDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(InternalSerializationApi::class)
data class SearchResultPage(
    val releases: List<ReleaseSearchWithFavorite>,
    val hasNextPage: Boolean,
)

@OptIn(InternalSerializationApi::class)
@Singleton
class ConsumeReleasesSearchUseCase @Inject constructor(
    private val releasesSearchRepository: ReleaseSearchRepository,
    private val releasesSearchDomainMapper: ReleaseSearchDomainMapper,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(title: String, page: Int): Flow<SearchResultPage> {
        return combine(
            releasesSearchRepository.consumeReleaseSearch(title, page),
            favoritesRepository.consumeReleaseIds()
        ) { response, releaseIds ->
            val releases = response.results.map { release: ReleaseResultDto ->
                ReleaseSearchWithFavorite(
                    release = releasesSearchDomainMapper.fromEntity(release),
                    isFavorite = release.id.toString() in releaseIds
                )
            }
            SearchResultPage(
                releases = releases,
                hasNextPage = response.pagination.urls.next != null
            )
        }
    }
}
