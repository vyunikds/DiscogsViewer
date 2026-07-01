package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FullReleaseDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface ReleaseDao {

    @Transaction
    @Query("SELECT * FROM releases ORDER BY community_have DESC")
    fun getAllReleases(): Flow<List<FullReleaseDbo>>

    @Query("SELECT thumb FROM releases WHERE release_id = :releaseId")
    suspend fun getThumb(releaseId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleases(releases: List<ReleaseDbo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenres(genres: List<GenreDbo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountries(countries: List<CountryDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleaseGenres(releaseGenres: List<ReleaseGenreDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleaseCountries(releaseCountries: List<ReleaseCountryDbo>)
}
