@file:OptIn(InternalSerializationApi::class)
package com.example.discogsviewer.di

import com.example.network.api.ReleasesApiFetchException
import com.example.network.api.ReleasesApiService
import com.example.network.di.NetworkModule
import com.example.network.dto.ArtistDetail
import com.example.network.dto.Community
import com.example.network.dto.ImageDetail
import com.example.network.dto.ReleaseDetailsDto
import com.example.network.dto.ReleaseResultDto
import com.example.network.dto.SearchResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import kotlinx.serialization.InternalSerializationApi

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class],
)
object TestNetworkModule {
    @Provides
    @Singleton
    fun provideReleasesApiService(): ReleasesApiService =
        object : ReleasesApiService {
            override suspend fun getReleases(): List<ReleaseResultDto> =
                throw ReleasesApiFetchException()

            override suspend fun searchReleases(
                title: String,
                page: Int,
            ): SearchResponse = throw ReleasesApiFetchException()

            override suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto =
                ReleaseDetailsDto(
                    id = releaseId,
                    title = "The Dark Side of the Moon",
                    artists = listOf(ArtistDetail(name = "Pink Floyd")),
                    artistsNames = listOf("Pink Floyd"),
                    country = "UK",
                    genres = listOf("Progressive Rock"),
                    coverImage = "",
                    thumb = "",
                    images = listOf(ImageDetail(uri = "")),
                    community = Community(have = 5000, want = 3000),
                )
        }
}
