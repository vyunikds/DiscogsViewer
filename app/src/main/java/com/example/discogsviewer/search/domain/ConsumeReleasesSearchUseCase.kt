package com.example.discogsviewer.search.domain

import com.example.favorite.FavoritesRepository
import com.example.search.ReleaseSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsumeReleasesSearchUseCase @Inject constructor(
    private val releasesSearchRepository: ReleaseSearchRepository,
    private val releasesSearchDomainMapper: ReleaseSearchDomainMapper,
    private val favoritesRepository: FavoritesRepository,
) {
    @OptIn(InternalSerializationApi::class)
    operator fun invoke(title: String): Flow<List<ReleaseSearchWithFavorite>> {
        return combine(
            releasesSearchRepository.consumeReleaseSearch(title),
            favoritesRepository.consumeReleaseIds()
        ) { releases, releaseIds ->
            releases
                .map { release ->
                    ReleaseSearchWithFavorite(
                        release = releasesSearchDomainMapper.fromEntity(release),
                        isFavorite = release.id.toString() in releaseIds
                    )
                }
        }
    }
}