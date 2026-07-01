package com.example.feature.details.domain

import com.example.favorite.FavoritesRepository
import com.example.releases.ReleasesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConsumeReleaseDetailsUseCase @Inject constructor(
    private val releasesRepository: ReleasesRepository,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(releaseId: String): Flow<ReleaseDetails> = flow {
        val model = releasesRepository.getReleaseDetails(releaseId.toInt())
        val releaseIds = favoritesRepository.consumeReleaseIds().first()
        emit(
            ReleaseDetails(
                id = model.id,
                releaseTitle = model.releaseTitle,
                artistTitle = model.artistTitle,
                country = model.country,
                genres = model.genres,
                coverImage = model.coverImage,
                isFavorite = releaseId in releaseIds,
                want = model.want,
                have = model.have,
            )
        )
    }.flowOn(Dispatchers.IO)
}
