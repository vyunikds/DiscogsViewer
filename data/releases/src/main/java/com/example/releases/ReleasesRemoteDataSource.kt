package com.example.releases

import com.example.network.api.ReleasesApiService
import com.example.network.dto.ReleaseDetailsDto
import com.example.network.dto.ReleaseResultDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.InternalSerializationApi

@Singleton
class ReleasesRemoteDataSource
    @Inject
    constructor(
        private val provideReleasesApiService: ReleasesApiService,
    ) {
        @OptIn(InternalSerializationApi::class)
        suspend fun getReleases(): List<ReleaseResultDto> = provideReleasesApiService.getReleases()

        @Suppress("MaxLineLength")
        @OptIn(InternalSerializationApi::class)
        suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto = provideReleasesApiService.getReleaseById(releaseId)
    }
