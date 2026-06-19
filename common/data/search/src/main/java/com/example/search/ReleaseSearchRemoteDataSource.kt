package com.example.search

import com.example.network.api.ReleasesApiService
import com.example.network.dto.ReleaseResultDto
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleaseSearchRemoteDataSource @Inject constructor(
    private val provideReleasesApiService: ReleasesApiService,
) {
    @OptIn(InternalSerializationApi::class)
    suspend fun searchRelease(title: String): List<ReleaseResultDto> {
        return provideReleasesApiService.searchReleases(title)
    }
}
