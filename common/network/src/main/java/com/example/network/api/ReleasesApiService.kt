package com.example.network.api

import com.example.network.dto.ReleaseDetailsDto
import com.example.network.dto.ReleaseResultDto
import kotlinx.serialization.InternalSerializationApi

interface ReleasesApiService {
    @OptIn(InternalSerializationApi::class)
    suspend fun getReleases(): List<ReleaseResultDto>

    @OptIn(InternalSerializationApi::class)
    suspend fun searchReleases(title: String): List<ReleaseResultDto>

    @OptIn(InternalSerializationApi::class)
    suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto
}
