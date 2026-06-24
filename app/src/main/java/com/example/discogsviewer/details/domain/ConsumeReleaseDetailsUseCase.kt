package com.example.discogsviewer.details.domain

import com.example.favorite.FavoritesRepository
import com.example.releases.ReleasesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
class ConsumeReleaseDetailsUseCase @Inject constructor(
    private val releasesRepository: ReleasesRepository,
    private val releasesDetailsDomainMapper: ReleaseDetailsDomainMapper,
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(releaseId: String): Flow<ReleaseDetails> = flow {
        val dto = releasesRepository.getReleaseById(releaseId.toInt())
        val releaseIds = favoritesRepository.consumeReleaseIds().first()
        val details = releasesDetailsDomainMapper.fromDto(dto)
        emit(
            details.copy(
                isFavorite = releaseId in releaseIds
            )
        )
    }.flowOn(Dispatchers.IO)
}
