package com.example.releases

import android.util.Log
import com.example.database.dbo.TopReleasesDbo
import com.example.network.dto.ReleaseDetailsDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ReleasesRepository @Inject constructor(
    private val releaseLocalDataSource: ReleasesLocalDataSource,
    private val releaseRemoteDataSource: ReleasesRemoteDataSource,
    private val releaseDataMapper: ReleaseDataMapper,
    @Named("ioDispatcher") private val dispatcher: CoroutineDispatcher,
) {
    private var hasFetchedRelease = false

    @OptIn(InternalSerializationApi::class)
    fun observeReleases(): Flow<List<TopReleasesDbo>> = releaseLocalDataSource.consumeReleases()

    @OptIn(InternalSerializationApi::class)
    suspend fun fetchAndSave() {
        val releases = releaseRemoteDataSource.getReleases()
        releaseLocalDataSource.saveReleases(
            releases.map(releaseDataMapper::toDbo)
        )
    }

    @OptIn(InternalSerializationApi::class)
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
