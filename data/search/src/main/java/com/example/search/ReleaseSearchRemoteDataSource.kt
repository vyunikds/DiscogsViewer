package com.example.search

import com.example.network.api.ReleasesApiService
import com.example.network.dto.SearchResponse
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.InternalSerializationApi

@Singleton
class ReleaseSearchRemoteDataSource
    @Inject
    constructor(
        private val provideReleasesApiService: ReleasesApiService,
    ) {
        @OptIn(InternalSerializationApi::class)
        suspend fun searchRelease(
            title: String,
            page: Int,
        ): SearchResponse = provideReleasesApiService.searchReleases(title, page)
    }
