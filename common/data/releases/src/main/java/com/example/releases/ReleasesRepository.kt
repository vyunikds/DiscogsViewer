package com.example.releases

import android.util.Log
import com.example.database.dbo.FullReleaseDbo
import com.example.network.dto.ReleaseDetailsDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@OptIn(InternalSerializationApi::class)
@Singleton
class ReleasesRepository @Inject constructor(
    private val releaseLocalDataSource: ReleasesLocalDataSource,
    private val releaseRemoteDataSource: ReleasesRemoteDataSource,
    private val releaseDataMapper: ReleaseDataMapper,
    @Named("ioDispatcher") private val dispatcher: CoroutineDispatcher,
) {
    private var hasFetchedRelease = false

    fun observeReleases(): Flow<List<FullReleaseDbo>> = releaseLocalDataSource.consumeReleases()

    suspend fun fetchAndSave() {
        val releases = releaseRemoteDataSource.getReleases()
        val dbos = releaseDataMapper.toDbos(releases)
        val genres = releaseDataMapper.toReleaseGenresBatch(releases)
        val countries = releaseDataMapper.toReleaseCountriesBatch(releases)
        releaseLocalDataSource.saveReleases(dbos, genres, countries)
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

    suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto {
        return releaseRemoteDataSource.getReleaseById(releaseId)
    }
}
