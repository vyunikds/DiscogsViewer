package com.example.feature.releases.domain

import com.example.favorite.FavoritesRepository
import com.example.releases.ReleasesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsumeReleasesUseCase @Inject constructor(
    private val releasesRepository: ReleasesRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<ReleaseWithFavorite>> {
        return kotlinx.coroutines.flow.combine(
            releasesRepository.observeTopReleasesAsModel(),
            favoritesRepository.consumeReleaseIds()
        ) { releaseModels: List<com.example.releases.domain.ReleaseDboModel>, releaseIds ->
            releaseModels
                .map { releaseModel ->
                    ReleaseWithFavorite(
                        release = Release(
                            id = releaseModel.id,
                            artistTitle = releaseModel.artistTitle,
                            releaseTitle = releaseModel.releaseTitle,
                            country = releaseModel.country,
                            genre = releaseModel.genres,
                            thumb = releaseModel.thumb,
                            coverImage = releaseModel.coverImage,
                        ),
                        isFavorite = releaseModel.id in releaseIds
                    )
                }
        }
    }
}
