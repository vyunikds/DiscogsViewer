package com.example.discogsviewer.releases.domain

import com.example.database.dbo.FullReleaseDbo
import com.example.favorite.FavoritesRepository
import com.example.releases.ReleasesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsumeReleasesUseCase @Inject constructor(
    private val releasesRepository: ReleasesRepository,
    private val releasesDomainMapper: ReleaseDomainMapper,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<ReleaseWithFavorite>> {
        return combine(
            releasesRepository.observeReleases(),
            favoritesRepository.consumeReleaseIds()
        ) { fullReleases: List<FullReleaseDbo>, releaseIds ->
            fullReleases
                .map { fullRelease ->
                    ReleaseWithFavorite(
                        release = releasesDomainMapper.fromEntity(fullRelease),
                        isFavorite = fullRelease.release.id in releaseIds
                    )
                }
        }
    }
}
