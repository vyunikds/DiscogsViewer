package com.example.releases

import com.example.network.api.ReleasesApiService
import com.example.network.dto.ReleaseDetailsDto
import com.example.network.dto.ReleaseResultDto
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleasesRemoteDataSource @Inject constructor(
    private val provideReleasesApiService: ReleasesApiService,
) {
    @OptIn(InternalSerializationApi::class)
    suspend fun getReleases(): List<ReleaseResultDto> {
        return provideReleasesApiService.getReleases()
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto {
        return provideReleasesApiService.getReleaseById(releaseId)
    }
}
