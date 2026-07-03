package com.example.releases

import android.util.Log
import com.example.database.dbo.TopReleaseDbo
import com.example.network.api.ReleasesApiFetchException
import com.example.releases.domain.ReleaseDboModel
import com.example.releases.domain.ReleaseDetailsModel
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi

data class ReleaseSaveData(
    val id: String,
    val artistTitle: String,
    val releaseTitle: String,
    val thumb: String,
    val coverImage: String,
    val genres: List<String>,
    val country: String,
    val communityHave: Int = 0,
    val communityWant: Int = 0,
)

@Singleton
class ReleasesRepository
    @Inject
    constructor(
        private val releaseLocalDataSource: ReleasesLocalDataSource,
        private val topReleasesLocalDataSource: TopReleasesLocalDataSource,
        private val releaseRemoteDataSource: ReleasesRemoteDataSource,
        private val releaseDataMapper: ReleaseDataMapper,
    ) {
        private var hasFetchedRelease = false

        fun observeTopReleasesAsModel(): Flow<List<ReleaseDboModel>> =
            topReleasesLocalDataSource
                .consumeTopReleases()
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
                releaseCountries = releases.flatMap(releaseDataMapper::toReleaseCountries),
            )
            topReleasesLocalDataSource.clearTopReleases()
            topReleasesLocalDataSource.saveTopReleases(
                releases.map { TopReleaseDbo(it.id.toString()) },
            )
        }

        suspend fun fetchAndSaveIfNeeded() {
            if (hasFetchedRelease) return
            hasFetchedRelease = true
            try {
                fetchAndSave()
            } catch (e: ReleasesApiFetchException) {
                hasFetchedRelease = false
                Log.w("ReleasesRepository", "API fetch failed: ${e.message}")
            } catch (e: SQLException) {
                hasFetchedRelease = false
                Log.w("ReleasesRepository", "Database error: ${e.message}")
            } catch (e: kotlinx.serialization.SerializationException) {
                hasFetchedRelease = false
                Log.w("ReleasesRepository", "Serialization error: ${e.message}")
            }
        }

        suspend fun saveReleaseData(data: ReleaseSaveData) {
            val dbo =
                com.example.database.dbo.ReleaseDbo(
                    id = data.id,
                    artistTitle = data.artistTitle,
                    releaseTitle = data.releaseTitle,
                    thumb = data.thumb,
                    coverImage = data.coverImage,
                    communityHave = data.communityHave,
                    communityWant = data.communityWant,
                )
            val genreDbos =
                data.genres.map {
                    com.example.database.dbo
                        .ReleaseGenreDbo(data.id, it)
                }
            val countryDbos =
                listOf(
                    com.example.database.dbo
                        .ReleaseCountryDbo(data.id, data.country),
                )
            releaseLocalDataSource.saveReleases(listOf(dbo), genreDbos, countryDbos)
        }

        @OptIn(InternalSerializationApi::class)
        suspend fun getReleaseDetails(releaseId: Int): ReleaseDetailsModel {
            val dto = releaseRemoteDataSource.getReleaseById(releaseId)
            val artistName =
                dto.artistsNames.firstOrNull { it.isNotBlank() }
                    ?: dto.artists.firstOrNull { it.name.isNotBlank() }?.name
                    ?: ""
            val community = dto.community
            val coverImage =
                dto.images.firstOrNull { it.type == "primary" }?.uri
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
