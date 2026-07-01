package com.example.releases

import android.util.Log
import com.example.database.dbo.FullReleaseDbo
import com.example.database.dbo.TopReleaseDbo
import com.example.network.dto.ReleaseDetailsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleasesRepository @Inject constructor(
    private val releaseLocalDataSource: ReleasesLocalDataSource,
    private val topReleasesLocalDataSource: TopReleasesLocalDataSource,
    private val releaseRemoteDataSource: ReleasesRemoteDataSource,
    private val releaseDataMapper: ReleaseDataMapper,
) {
    private var hasFetchedRelease = false

    fun observeTopReleases(): Flow<List<FullReleaseDbo>> = topReleasesLocalDataSource.consumeTopReleases()

    @OptIn(InternalSerializationApi::class)
    suspend fun fetchAndSave() {
        val releases = releaseRemoteDataSource.getReleases()
        releaseLocalDataSource.saveReleases(
            releases = releases.map(releaseDataMapper::toDbo),
            releaseGenres = releases.flatMap(releaseDataMapper::toReleaseGenres),
            releaseCountries = releases.flatMap(releaseDataMapper::toReleaseCountries)
        )
        topReleasesLocalDataSource.clearTopReleases()
        topReleasesLocalDataSource.saveTopReleases(
            releases.map { TopReleaseDbo(it.id.toString()) }
        )
    }

    suspend fun fetchAndSaveIfNeeded() {
        if (hasFetchedRelease) return
        hasFetchedRelease = true
        try {
            fetchAndSave()
        } catch (e: Exception) {
            Log.w("ReleasesRepository", e)
        }
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto {
        return releaseRemoteDataSource.getReleaseById(releaseId)
    }
}
