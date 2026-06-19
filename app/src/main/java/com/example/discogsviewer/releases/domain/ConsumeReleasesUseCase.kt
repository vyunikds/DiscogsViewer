package com.example.discogsviewer.releases.domain

import com.example.favorite.FavoritesRepository
import com.example.releases.ReleasesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsumeReleasesUseCase @Inject constructor(
    private val releasesRepository: ReleasesRepository,
    private val releasesDomainMapper: ReleaseDomainMapper,
    private val favoritesRepository: FavoritesRepository,
) {
    @OptIn(InternalSerializationApi::class)
    operator fun invoke(): Flow<List<ReleaseWithFavorite>> {
        return combine(
            releasesRepository.observeReleases(),
            favoritesRepository.consumeReleaseIds()
        ) { releases, releaseIds ->
            releases
                .map { release ->
                    ReleaseWithFavorite(
                        release = releasesDomainMapper.fromEntity(release),
                        isFavorite = release.id.toString() in releaseIds
                    )
                }
        }
    }
}