package com.example.releases

import android.util.Log
import com.example.database.dbo.TopReleaseDbo
import com.example.releases.domain.ReleaseDboModel
import com.example.releases.domain.ReleaseDetailsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    fun observeTopReleasesAsModel(): Flow<List<ReleaseDboModel>> = topReleasesLocalDataSource.consumeTopReleases()
        .map { fullReleases ->
            fullReleases.map { fullRelease ->
                ReleaseDboModel(
                    id = fullRelease.release.id,
                    artistTitle = fullRelease.release.artistTitle,
                    releaseTitle = fullRelease.release.releaseTitle,
                    country = fullRelease.countriesList.firstOrNull()?.country ?: "",
                    genres = fullRelease.genresList.map { it.genre },
                    thumb = fullRelease.release.thumb,
                    coverImage = fullRelease.release.coverImage,
                    communityHave = fullRelease.release.communityHave,
                    communityWant = fullRelease.release.communityWant,
                )
            }
        }

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

    suspend fun saveReleaseData(
        id: String,
        artistTitle: String,
        releaseTitle: String,
        thumb: String,
        coverImage: String,
        genres: List<String>,
        country: String,
        communityHave: Int = 0,
        communityWant: Int = 0,
    ) {
        val dbo = com.example.database.dbo.ReleaseDbo(
            id = id,
            artistTitle = artistTitle,
            releaseTitle = releaseTitle,
            thumb = thumb,
            coverImage = coverImage,
            communityHave = communityHave,
            communityWant = communityWant,
        )
        val genreDbos = genres.map { com.example.database.dbo.ReleaseGenreDbo(id, it) }
        val countryDbos = listOf(com.example.database.dbo.ReleaseCountryDbo(id, country))
        releaseLocalDataSource.saveReleases(listOf(dbo), genreDbos, countryDbos)
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun getReleaseDetails(releaseId: Int): ReleaseDetailsModel {
        val dto = releaseRemoteDataSource.getReleaseById(releaseId)
        val artistName = dto.artistsNames.firstOrNull { it.isNotBlank() }
            ?: dto.artists.firstOrNull { it.name.isNotBlank() }?.name
            ?: ""
        val community = dto.community
        val coverImage = dto.images.firstOrNull { it.type == "primary" }?.uri
            ?: dto.images.firstOrNull()?.uri
            ?: dto.thumb
        return ReleaseDetailsModel(
            id = dto.id.toString(),
            releaseTitle = dto.title,
            artistTitle = artistName,
            country = dto.country,
            genres = dto.genres,
            coverImage = coverImage,
            want = community?.want ?: 0,
            have = community?.have ?: 0,
        )
    }
}
