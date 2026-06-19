package com.example.releases

import com.example.database.dao.TopReleasesDao
import com.example.database.dbo.TopReleasesDbo
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleasesLocalDataSource @Inject constructor(
    private val discogsDao: TopReleasesDao,
) {
    @OptIn(InternalSerializationApi::class)
    fun consumeReleases(): Flow<List<TopReleasesDbo>> = discogsDao.getAllReleases()

    @OptIn(InternalSerializationApi::class)
    suspend fun saveReleases(releases: List<TopReleasesDbo>) {
        discogsDao.insertReleases(releases)
    }

    suspend fun getThumb(releaseId: Int): String? = discogsDao.getThumb(releaseId)
}