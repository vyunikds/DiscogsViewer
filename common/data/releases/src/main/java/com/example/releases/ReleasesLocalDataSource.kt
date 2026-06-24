package com.example.releases

import androidx.room.Transaction
import com.example.database.dao.ReleaseDao
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FullReleaseDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleasesLocalDataSource @Inject constructor(
    private val releaseDao: ReleaseDao,
) {
    fun consumeReleases(): Flow<List<FullReleaseDbo>> = releaseDao.getAllReleases()

    @Transaction
    suspend fun saveReleases(
        releases: List<ReleaseDbo>,
        releaseGenres: List<ReleaseGenreDbo>,
        releaseCountries: List<ReleaseCountryDbo>
    ) {
        releaseDao.insertGenres(toGenres(releaseGenres))
        releaseDao.insertCountries(toCountries(releaseCountries))
        releaseDao.insertReleases(releases)
        releaseDao.insertReleaseGenres(releaseGenres)
        releaseDao.insertReleaseCountries(releaseCountries)
    }

    suspend fun getThumb(releaseId: String): String? = releaseDao.getThumb(releaseId)

    private fun toGenres(releaseGenres: List<ReleaseGenreDbo>): List<GenreDbo> =
        releaseGenres.map { GenreDbo(it.genre) }.distinctBy { it.genre }

    private fun toCountries(releaseCountries: List<ReleaseCountryDbo>): List<CountryDbo> =
        releaseCountries.map { CountryDbo(it.country) }.distinctBy { it.country }
}
