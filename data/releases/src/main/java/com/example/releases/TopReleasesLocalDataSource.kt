package com.example.releases

import com.example.database.dao.TopReleaseDao
import com.example.database.dbo.FullReleaseDbo
import com.example.database.dbo.TopReleaseDbo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class TopReleasesLocalDataSource
    @Inject
    constructor(
        private val topReleaseDao: TopReleaseDao,
    ) {
        fun consumeTopReleases(): Flow<List<FullReleaseDbo>> = topReleaseDao.getTopReleases()

        suspend fun saveTopReleases(topReleases: List<TopReleaseDbo>) {
            topReleaseDao.insertAll(topReleases)
        }

        suspend fun clearTopReleases() {
            topReleaseDao.clear()
        }
    }
